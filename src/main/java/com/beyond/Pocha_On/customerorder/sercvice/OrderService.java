package com.beyond.Pocha_On.customerorder.sercvice;


import com.beyond.Pocha_On.cart.service.CartService;
import com.beyond.Pocha_On.cart.domain.RedisCartItem;
import com.beyond.Pocha_On.customerorder.dto.*;
import com.beyond.Pocha_On.customerTable.domain.CustomerTable;
import com.beyond.Pocha_On.customerTable.repository.CustomerTableRepository;
import com.beyond.Pocha_On.menu.domain.Menu;
import com.beyond.Pocha_On.menu.domain.MenuOption;
import com.beyond.Pocha_On.menu.repository.MenuOptionRepository;
import com.beyond.Pocha_On.menu.repository.MenuRepository;
import com.beyond.Pocha_On.ordering.domain.OrderStatus;
import com.beyond.Pocha_On.ordering.domain.Ordering;
import com.beyond.Pocha_On.ordering.domain.OrderingDetail;
import com.beyond.Pocha_On.ordering.domain.OrderingDetailOption;
import com.beyond.Pocha_On.ordering.dto.OrderQueueDto;
import com.beyond.Pocha_On.ordering.repository.OrderingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final CartService cartService;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final OrderingRepository orderingRepository;
    @Qualifier("idempotencyRedisTemplate")
    private final RedisTemplate<String, String> idempotencyRedisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final CustomerTableRepository customerTableRepository;
    @Qualifier("groupRedisTemplate")
    private final RedisTemplate<String, String> groupRedisTemplate;

    @Autowired
    public OrderService(CartService cartService, MenuRepository menuRepository, MenuOptionRepository menuOptionRepository, OrderingRepository orderingRepository, @Qualifier("idempotencyRedisTemplate") RedisTemplate<String, String> idempotencyRedisTemplate, SimpMessagingTemplate messagingTemplate, CustomerTableRepository customerTableRepository,@Qualifier("groupRedisTemplate") RedisTemplate<String, String> groupRedisTemplate) {
        this.cartService = cartService;
        this.menuRepository = menuRepository;
        this.menuOptionRepository = menuOptionRepository;
        this.orderingRepository = orderingRepository;
        this.idempotencyRedisTemplate = idempotencyRedisTemplate;
        this.messagingTemplate = messagingTemplate;
        this.customerTableRepository = customerTableRepository;
        this.groupRedisTemplate = groupRedisTemplate;
    }

    //  멱등성 로직
    private UUID idempotencyCheck(String redisKey, UUID idempotencyKey) {

//      멱등성생성(redis)
        Boolean locked = idempotencyRedisTemplate.opsForValue()
                .setIfAbsent(redisKey, "Lock", Duration.ofSeconds(3));

//        중복 시
        if (Boolean.FALSE.equals(locked)) {
            Ordering duplicated = orderingRepository.findByIdempotencyKey(idempotencyKey);
            if (duplicated != null) {
                return duplicated.getGroupId();
            }
            throw new IllegalArgumentException("이미 처리 중인 주문입니다");
        }

//      멱등성(db)
        Ordering duplicated = orderingRepository.findByIdempotencyKey(idempotencyKey);
        if (duplicated != null) {
            return duplicated.getGroupId();
        }

        return null;
    }


    // 주문공통로직
    private UUID createOrderInternal(OrderCreateDto createDto, UUID groupId) {

//        redis cart조회
        List<RedisCartItem> cartItemList = cartService.cartItems(createDto.getTableId());
        if (cartItemList.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비었습니다");
        }

//      테이블 조회
        CustomerTable customerTable = customerTableRepository.findById(createDto.getTableId()).orElseThrow(() -> new IllegalArgumentException("없는 테이블"));


//       주문생성
        Ordering ordering = Ordering.builder()
                .customerTable(customerTable)
                .groupId(groupId)
                .idempotencyKey(createDto.getIdempotencyKey())
                .orderStatus(OrderStatus.STANDBY)
                .build();

        int totalPrice = 0;

//        주문상세 // cart에서 값 받아오기
        for (RedisCartItem cartItem : cartItemList) {
            Menu menu = menuRepository.findById(cartItem.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("없는 메뉴"));
            int quantity = cartItem.getQuantity();

            OrderingDetail detail = OrderingDetail.builder()
                    .ordering(ordering)
                    .menu(menu)
                    .orderingDetailQuantity(quantity)
                    .build();


//            옵션
            String optionKey = cartItem.getOptionKey(); // ("\\|,2)[1]한 값
            if (optionKey != null) {
                String[] optionArr = optionKey.split(","); // option끼리 구별
                for (String opt : optionArr) {
                    Long optionId = Long.parseLong(opt.trim());
                    MenuOption option = menuOptionRepository.findById(optionId)
                            .orElseThrow(() -> new IllegalArgumentException("없는 옵션입니다"));
//                    optionDto 조립
                    OrderingDetailOption optionSnap = OrderingDetailOption.builder()
                            .orderingOptionName(option.getOptionName())
                            .orderingDetail(detail)
                            .orderingOptionPrice(option.getOptionPrice())
                            .build();

                    detail.getOrderingDetailOptions().add(optionSnap);
                }
            }
            ordering.getOrderDetail().add(detail);

//            totalPrice금액누적
            int unitPrice = cartItem.getUnitPrice();
            totalPrice += unitPrice * quantity;
        }
//        주문 총액 스냅샷
        ordering.setTotalPrice(totalPrice);

//        db저장
        orderingRepository.save(ordering);

//          실시간 주문 알림(테이블 단위)
//            // ========== 점주 화면에 실시간 알림 ==========
        Long storeId = customerTable.getStore().getId();
        messagingTemplate.convertAndSend(
                "/topic/order-queue/" + storeId,
                Map.of(
                        "type", "NEW_ORDER",
                        "order", OrderQueueDto.fromEntity(ordering)
                )
        );


//        카트 비우기
        cartService.CartClear(createDto.getTableId());

        return groupId;
    }



    //  1.주문생성
    public UUID create(OrderCreateDto createDto) {

        String redisKey = "idempotency:order:create:" + createDto.getIdempotencyKey();

//      멱등성(redis) -동시성 제어
        UUID duplicatedGroupId = idempotencyCheck(redisKey, createDto.getIdempotencyKey());

        if (duplicatedGroupId != null) {
            return duplicatedGroupId;
        }

        try {

//          redis. group uuid
            String groupKey = "order:group:" + createDto.getTableId();
            String groupValue = groupRedisTemplate.opsForValue().get(groupKey);

//      주문묶음
            UUID groupId;
            if (groupValue != null) {
                groupId = UUID.fromString(groupValue);
            } else {
                groupId = UUID.randomUUID();
            }

            UUID result = createOrderInternal(createDto, groupId);

//            groupId redis저장
            groupRedisTemplate.opsForValue()
                    .set(groupKey, groupId.toString(), Duration.ofHours(4));


//       db저장 성공 시 상태 확인용- 재전송방지
            idempotencyRedisTemplate.opsForValue()
                    .set(redisKey, "SUCCESS", Duration.ofMinutes(1));

            return result;

        } catch (Exception e) {
            idempotencyRedisTemplate.delete(redisKey);
            throw e;
        }
    }


    //    2. 추가주문
    public UUID add(OrderAddDto addDto) {
        String redisKey = "idempotency:order:add:" + addDto.getIdempotencyKey();
//      멱등성 처리
        UUID duplicateGroupId = idempotencyCheck(redisKey, addDto.getIdempotencyKey());
        if (duplicateGroupId != null) {
            return duplicateGroupId;
        }
        //      기존 주문 유무 조회
        try {
            List<Ordering> exist = orderingRepository.findByGroupId(addDto.getGroupId());
            if (exist == null || exist.isEmpty()) {
                throw new IllegalArgumentException("존재하지 않는 GroupId입니다");
            }

//            addDto -> createDto
            OrderCreateDto createDto = OrderCreateDto.builder()
                    .groupId(addDto.getGroupId())
                    .tableId(addDto.getTableId())
                    .idempotencyKey(addDto.getIdempotencyKey())
                    .build();

//            주문생성로직 호출
            UUID result = createOrderInternal(createDto, addDto.getGroupId());

//          db저장 성공 시 상태 확인용
            idempotencyRedisTemplate.opsForValue()
                    .set(redisKey, "SUCCESS", Duration.ofMinutes(1));

            return result;

        } catch (Exception e) {

//       주문처리중 예외 발생시 재시도
            idempotencyRedisTemplate.delete(redisKey);

            throw e;
        }
    }



    //    3. 주문내역 조회
    public List<OrderListDto> list(UUID groupId) {
        List<Ordering> orderings = orderingRepository.findByGroupId(groupId);
        if (orderings.isEmpty() || orderings == null) {
            throw new IllegalArgumentException("주문내역이 없습니다");
        }
        List<OrderListDto> result = new ArrayList<>();
//        주문정보
        for (Ordering ordering : orderings) {

            List<OrderListDetailDto> detailDtos = new ArrayList<>();
            int totalPrice = 0;
//          주문 상세 (메뉴)
            for (OrderingDetail detail : ordering.getOrderDetail()) {
                int basePrice = detail.getMenu().getPrice();
                int optionSum = 0;
//                주문 옵션
                for (OrderingDetailOption opt : detail.getOrderingDetailOptions()) {
                    optionSum += opt.getOrderingOptionPrice();
                }
//                라인가격
                int quantity = detail.getOrderingDetailQuantity();
                int linePrice = (basePrice + optionSum) * quantity;
                totalPrice += linePrice;

//                옵션 Dto조립
                List<OrderListDetailOpDto> optionDto = new ArrayList<>();
                for (OrderingDetailOption opt : detail.getOrderingDetailOptions()) {
                    optionDto.add(OrderListDetailOpDto.builder()
                            .optionId(opt.getId())
                            .build());
                }

//                detail dto조립
                OrderListDetailDto detailDto = OrderListDetailDto.builder()
                        .menuId(detail.getMenu().getId())
                        .menuQuantity(quantity)
                        .linePrice(linePrice)
                        .orderDetailOpDto(optionDto)
                        .build();
                detailDtos.add(detailDto);
            }

//                주문 dto
            OrderListDto orderDto =
                    OrderListDto.builder()
                            .tableId(ordering.getCustomerTable().getCustomerTableId())
                            .groupId(ordering.getGroupId())
                            .totalPrice(totalPrice)
                            .listDetailDto(detailDtos)
                            .build();

            result.add(orderDto);
        }

        return result;
    }


}