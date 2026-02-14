package com.beyond.Pocha_On.owner.dtos;

import com.beyond.Pocha_On.owner.domain.Owner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MyPageResDto {

    private Long ownerId;
    private Integer BusinessRegistrationNumber;
    private String ownerName;
    private String ownerEmail;
    private String phoneNumber;

    public static MyPageResDto fromEntity(Owner owner) {
        return MyPageResDto.builder()
                .ownerId(owner.getId())
                .BusinessRegistrationNumber(owner.getBusinessRegistrationNumber())
                .ownerName(owner.getOwnerName())
                .ownerEmail(owner.getOwnerEmail())
                .phoneNumber(owner.getPhoneNumber())
                .build();
    }
}
