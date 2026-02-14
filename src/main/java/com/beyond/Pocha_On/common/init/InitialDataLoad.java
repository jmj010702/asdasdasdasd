package com.beyond.Pocha_On.common.init;

import com.beyond.Pocha_On.customerTable.domain.CustomerTable;
import com.beyond.Pocha_On.customerTable.domain.TableStatus;
import com.beyond.Pocha_On.customerTable.repository.CustomerTableRepository;
import com.beyond.Pocha_On.owner.domain.Owner;
import com.beyond.Pocha_On.owner.domain.Role;
import com.beyond.Pocha_On.owner.repository.OwnerRepository;
import com.beyond.Pocha_On.store.domain.Store;
import com.beyond.Pocha_On.store.repository.StoreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Component
@Transactional
public class InitialDataLoad implements CommandLineRunner {
    private final OwnerRepository ownerRepository;
        private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;
    private final CustomerTableRepository customerTableRepository;

    public InitialDataLoad(OwnerRepository ownerRepository, StoreRepository storeRepository, CustomerTableRepository customerTableRepository, PasswordEncoder passwordEncoder) {
        this.ownerRepository = ownerRepository;
        this.storeRepository = storeRepository;
        this.customerTableRepository = customerTableRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (ownerRepository.findByOwnerEmail("admin@naver.com").isPresent()) return;
        ownerRepository.save(Owner.builder()
                .ownerName("admin")
                .ownerEmail("admin@naver.com")
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("12341234")).build());

        Owner owner1 = Owner.builder()
                .ownerEmail("owner1@test.com")
                .password("password123")  // 실제로는 암호화 필요
                .ownerName("김점주")
                .phoneNumber("010-1234-5678")
                .BusinessRegistrationNumber(1234567890)
                .role(Role.OWNER)
                .build();
        ownerRepository.save(owner1);
        Owner owner2 = Owner.builder()
                .ownerEmail("owner2@test.com")
                .password("password123")
                .ownerName("이점주")
                .phoneNumber("010-9876-5432")
                .BusinessRegistrationNumber(987654321)
                .role(Role.OWNER)
                .build();
        ownerRepository.save(owner2);
        Store store1 = Store.builder()
                .storeName("맛있는 식당")
                .storeOpenAt(LocalTime.of(9, 0))
                .storeCloseAt(LocalTime.of(22, 0))
                .owner(owner1)
                .build();
        storeRepository.save(store1);
        Store store2 = Store.builder()
                .storeName("좋은 식당")
                .storeOpenAt(LocalTime.of(10, 0))
                .storeCloseAt(LocalTime.of(23, 0))
                .owner(owner2)
                .build();
        storeRepository.save(store2);
        for (int i = 1; i <= 5; i++) {
            CustomerTable table = CustomerTable.builder()
                    .store(store1)
                    .tableStatus(TableStatus.STANDBY)
                    .groupId(null)
                    .build();
            customerTableRepository.save(table);
        }
        for (int i = 1; i <= 5; i++) {
            CustomerTable table = CustomerTable.builder()
                    .store(store2)
                    .tableStatus(TableStatus.STANDBY)
                    .groupId(null)
                    .build();
            customerTableRepository.save(table);

            System.out.println("✅ 초기 데이터 생성 완료!");
            System.out.println("   - Owner: 2명");
            System.out.println("   - Store: 2개");
            System.out.println("   - Table: 10개");
            System.out.println("\n📌 테스트용 계정:");
            System.out.println("   Email: owner1@test.com");
            System.out.println("   Email: owner2@test.com");
        }
    }
}


