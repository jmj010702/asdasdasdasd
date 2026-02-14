//package com.beyond.MyoungJin.common.init;
//
//import com.beyond.MyoungJin.customer_table.domain.CustomerTable;
//import com.beyond.MyoungJin.customer_table.repository.CustomerTableRepository;
//import com.beyond.MyoungJin.menu.domain.Category;
//import com.beyond.MyoungJin.menu.domain.Menu;
//import com.beyond.MyoungJin.menu.domain.MenuOption;
//import com.beyond.MyoungJin.menu.repository.MenuCategoryRepository;
//import com.beyond.MyoungJin.menu.repository.MenuOptionRepository;
//import com.beyond.MyoungJin.menu.repository.MenuRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
/// / cart, ordering
//@Component
//@Transactional
//public class InitialData implements CommandLineRunner {
//
//    private final CustomerTableRepository customerTableRepository;
//    private final MenuRepository menuRepository;
//    private final MenuOptionRepository menuOptionRepository;
//    private final MenuCategoryRepository menuCategoryRepository;
//
//    public InitialData(
//            CustomerTableRepository customerTableRepository,
//            MenuRepository menuRepository,
//            MenuOptionRepository menuOptionRepository,
//            MenuCategoryRepository menuCategoryRepository
//    ) {
//        this.customerTableRepository = customerTableRepository;
//        this.menuRepository = menuRepository;
//        this.menuOptionRepository = menuOptionRepository;
//        this.menuCategoryRepository = menuCategoryRepository;
//    }
//
//    @Override
//    public void run(String... args) {
//
//        if (menuRepository.count() > 0) return;
//
//        System.out.println("InitialData RUN");
//
//        // ==========================
//        // 1. 테이블 10개 생성
//        // ==========================
//        for (int i = 1; i <= 10; i++) {
//            CustomerTable table = CustomerTable.builder().build();
//            table.assignGroup(UUID.randomUUID());
//            customerTableRepository.save(table);
//        }
//
//        // ==========================
//        // 2. 카테고리 3개 생성
//        // ==========================
//        Category coffee = menuCategoryRepository.save(
//                Category.builder().categoryName("커피").build()
//        );
//
//        Category beverage = menuCategoryRepository.save(
//                Category.builder().categoryName("음료").build()
//        );
//
//        Category dessert = menuCategoryRepository.save(
//                Category.builder().categoryName("디저트").build()
//        );
//
//        // ==========================
//        // 3. 커피 메뉴 10개 생성
//        // ==========================
//        for (int i = 1; i <= 10; i++) {
//            Menu menu = menuRepository.save(
//                    Menu.builder()
//                            .menuName("커피메뉴" + i)
//                            .price(3000 + (i * 200))
//                            .category(coffee)
//                            .build()
//            );
//
//            // 옵션 3개
//            menuOptionRepository.save(
//                    MenuOption.builder()
//                            .menu(menu)
//                            .optionName("샷 추가")
//                            .optionPrice(500)
//                            .build()
//            );
//
//            menuOptionRepository.save(
//                    MenuOption.builder()
//                            .menu(menu)
//                            .optionName("시럽 추가")
//                            .optionPrice(300)
//                            .build()
//            );
//
//            menuOptionRepository.save(
//                    MenuOption.builder()
//                            .menu(menu)
//                            .optionName("사이즈 업")
//                            .optionPrice(700)
//                            .build()
//            );
//        }
//
//        // ==========================
//        // 4. 음료 메뉴 10개 생성
//        // ==========================
//        for (int i = 1; i <= 10; i++) {
//            Menu menu = menuRepository.save(
//                    Menu.builder()
//                            .menuName("음료메뉴" + i)
//                            .price(4000 + (i * 150))
//                            .category(beverage)
//                            .build()
//            );
//
//            menuOptionRepository.save(
//                    MenuOption.builder()
//                            .menu(menu)
//                            .optionName("얼음 적게")
//                            .optionPrice(0)
//                            .build()
//            );
//
//            menuOptionRepository.save(
//                    MenuOption.builder()
//                            .menu(menu)
//                            .optionName("당도 조절")
//                            .optionPrice(0)
//                            .build()
//            );
//
//            menuOptionRepository.save(
//                    MenuOption.builder()
//                            .menu(menu)
//                            .optionName("휘핑 추가")
//                            .optionPrice(500)
//                            .build()
//            );
//        }
//
//        // ==========================
//        // 5. 디저트 메뉴 10개 생성
//        // ==========================
//        for (int i = 1; i <= 10; i++) {
//            Menu menu = menuRepository.save(
//                    Menu.builder()
//                            .menuName("디저트메뉴" + i)
//                            .price(5000 + (i * 300))
//                            .category(dessert)
//                            .build()
//            );
//
//            menuOptionRepository.save(
//                    MenuOption.builder()
//                            .menu(menu)
//                            .optionName("토핑 추가")
//                            .optionPrice(700)
//                            .build()
//            );
//
//            menuOptionRepository.save(
//                    MenuOption.builder()
//                            .menu(menu)
//                            .optionName("아이스크림 추가")
//                            .optionPrice(1000)
//                            .build()
//            );
//
//            menuOptionRepository.save(
//                    MenuOption.builder()
//                            .menu(menu)
//                            .optionName("포장")
//                            .optionPrice(0)
//                            .build()
//            );
//        }
//
//        System.out.println("InitialData DONE");
//    }
//}