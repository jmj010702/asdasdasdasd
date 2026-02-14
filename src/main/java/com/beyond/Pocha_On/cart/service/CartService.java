package com.beyond.Pocha_On.cart.service;


import com.beyond.Pocha_On.cart.domain.RedisCartItem;
import com.beyond.Pocha_On.cart.dto.cart_dto.*;
import com.beyond.Pocha_On.menu.domain.Menu;
import com.beyond.Pocha_On.menu.domain.MenuOption;
import com.beyond.Pocha_On.menu.repository.MenuOptionRepository;
import com.beyond.Pocha_On.menu.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {


    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_PREFIX = "cart:";
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;

    //    optionKey받아오기 공통로직
    private String createFieldKey(Long menuId, List<Long> optionIds) {
        if(optionIds ==null) {
            optionIds = new ArrayList<>();
        }
        Collections.sort(optionIds);

        String optionPart = optionIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return  menuId + "|" + optionPart;
    }

    public CartService(@Qualifier("cartInventory")RedisTemplate<String, Object> redisTemplate, MenuRepository menuRepository, MenuOptionRepository menuOptionRepository) {
        this.redisTemplate = redisTemplate;
        this.menuRepository = menuRepository;
        this.menuOptionRepository = menuOptionRepository;
    }


    //    1. 카트 주문넣기
    public void cartCreate(CartCreateDto cartCreateDto) {

        Long tableId = cartCreateDto.getTableId();
        String redisKey = CART_PREFIX + tableId;
        HashOperations<String, String, RedisCartItem> hashOptions = redisTemplate.opsForHash();

        for (CartCreateDetailDto detailDto : cartCreateDto.getCreateDetailDtos()) {

            Menu menu = menuRepository.findById(detailDto.getMenuId()).orElseThrow(() -> new IllegalArgumentException("메뉴없음"));
            int menuPrice = menu.getPrice();

//            옵션 가격합산
            int optionPriceSum = 0;
            if (detailDto.getOptionIds() != null && !detailDto.getOptionIds().isEmpty()) {
                optionPriceSum = menuOptionRepository.sumPriceByOptionIds(detailDto.getOptionIds());
            }

            int unitPrice = menuPrice + optionPriceSum;

            Long menuId = detailDto.getMenuId();

            List<Long> optionId = detailDto.getOptionIds();
//          옵션기준으로 fieldKey생성

            String fieldKey = createFieldKey(menuId,optionId);
//            fieldKey를 기준으로 2번째 값(option)추출
            String optionKey = null;
            if(optionId!=null && !optionId.isEmpty()) {
                optionKey = fieldKey.split("\\|", 2)[1];
            }
//            redis에서 기존 값과 겹치는 것이 있는지 조회 후 없으면 저장, 있으면 수량 증가
            RedisCartItem checkItem = hashOptions.get(redisKey, fieldKey);
            if (checkItem == null) {
                RedisCartItem saveItem = RedisCartItem.builder()
                        .menuId(menuId)
                        .optionKey(optionKey)
                        .quantity(detailDto.getMenuQuantity())
                        .unitPrice(unitPrice)
                        .build();
                hashOptions.put(redisKey, fieldKey, saveItem);
            } else {
                checkItem.setQuantity(checkItem.getQuantity() + detailDto.getMenuQuantity());
                hashOptions.put(redisKey, fieldKey, checkItem);

            }
        }
    }


    //    2. 카트 조회
    public CartDto cartAll(Long tableId) {

        String redisKey = CART_PREFIX + tableId;
//      redis hash접근객체 생성
        HashOperations<String, String, RedisCartItem> hashOps = redisTemplate.opsForHash();
//        해당 장바구니 모든 항목 조회(빈카트 예외X)
        Map<String, RedisCartItem> cartItemMap = hashOps.entries(redisKey);
//        장바구니 상세목록
        List<CartDetailDto> cartDetailDtos = new ArrayList<>();

        int cartTotalPrice = 0;

        for (RedisCartItem item : cartItemMap.values()) {
//            메뉴조회
            Menu menu = menuRepository.findById(item.getMenuId()).orElseThrow(() -> new IllegalArgumentException("메뉴가 없습니다"));

//            옵션조회
            List<CartOptionDto> cartOptionDtos = new ArrayList<>();
            String optionKey = item.getOptionKey();

            if (optionKey != null) {

//              옵션값 배열로 변환, option값
                String[] optionArr = optionKey.split(",");
                for (String opt : optionArr) {
                    Long optionId = Long.parseLong(opt.trim());
                    MenuOption option = menuOptionRepository.findById(optionId).orElseThrow(() -> new IllegalArgumentException("없는 옵션입니다"));

                    CartOptionDto optionDto = new CartOptionDto(
                            option.getOptionName()
//                            option.getOptionPrice()
                    );
                    cartOptionDtos.add(optionDto);
                }
            }
            int lineTotalPrice = item.getUnitPrice() * item.getQuantity();

//                 CartDetailDto 생성
            CartDetailDto detailDto = new CartDetailDto(
                    item.getMenuId(),
                    menu.getMenuName(),
                    lineTotalPrice,
                    item.getQuantity(),
                    cartOptionDtos
            );

            cartDetailDtos.add(detailDto);
            cartTotalPrice += lineTotalPrice;
        }
//        CartDto생성
        return new CartDto(
                cartDetailDtos,
                cartTotalPrice
        );
    }
    //       N+1 리팩토링




    //    3. 카트 수정(수량변경)
    public void UpdateQuantity(CartUpdateDto updateDto){
        Long tableId = updateDto.getTableId();

        int delta = updateDto.getDelta();

        String redisKey = CART_PREFIX + tableId;
        HashOperations<String, String,RedisCartItem>hashops = redisTemplate.opsForHash();

//      fieldKey생성
        String fieldKey= createFieldKey(updateDto.getMenuId(),updateDto.getOptionIds());

//      예외
        RedisCartItem item =hashops.get(redisKey,fieldKey);
        if(item == null){
            throw new IllegalArgumentException("카트에 해당줄이 없습니다");
        }
//        수량계산
        int newQuantity =item.getQuantity()+delta;

        if(newQuantity<1){ //수량 1미만으로 조절 불가
            return;
        }

        item.setQuantity(newQuantity);

//        변경된 줄 redis에 저장
        hashops.put(redisKey,fieldKey,item);


    }


    //    4. 특정 줄 삭제
    public void LineDelete(CartLineDeleteDto lineDeleteDto){
        String redisKey =CART_PREFIX+lineDeleteDto.getTableId();
        String fieldKey = createFieldKey(lineDeleteDto.getMenuId(),lineDeleteDto.getOptionIds());
        redisTemplate.opsForHash().delete(redisKey,fieldKey);
    }

    //    5. 카트 비우기
    public void CartClear(Long tableId){
        String redisKey = CART_PREFIX+tableId;
        redisTemplate.delete(redisKey);
    }


    //    6. 주문전표 조회용
    public List<RedisCartItem> cartItems(Long tableId){
        String redisKey = CART_PREFIX + tableId;
//      field(id),redis(RedisCartItem) 추출
        Map<Object,Object>entries = redisTemplate.opsForHash().entries(redisKey);

//        장바구니가 비어있으면 빈리스트 반환
        if(entries == null || entries.isEmpty()){
            return List.of();
        }

        List<RedisCartItem>cartItems =new ArrayList<>();
        for(Object value: entries.values()){
            cartItems.add((RedisCartItem)value);
        }
        return cartItems;
    }

}






