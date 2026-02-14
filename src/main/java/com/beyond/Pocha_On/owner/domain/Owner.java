package com.beyond.Pocha_On.owner.domain;

import com.beyond.Pocha_On.common.BaseTimeEntity;
import com.beyond.Pocha_On.store.domain.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer BusinessRegistrationNumber; //사업자 등록 번호
    private String password;
    private String phoneNumber;
    private String ownerName;
    private String ownerEmail;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.OWNER;
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Store> storeList;


    public void changePassword(String password){
        this.password = password;
    }

    public void updatePhoneNum(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
