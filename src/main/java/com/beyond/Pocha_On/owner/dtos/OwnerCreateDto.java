package com.beyond.Pocha_On.owner.dtos;

import com.beyond.Pocha_On.owner.domain.Owner;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerCreateDto {
    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    private String password;
    @NotBlank(message = "전화번호는 필수 입력 사항입니다.")
    private String phoneNumber;
    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String ownerName;
    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    private String ownerEmail;

    public Owner toEntity(String encodedPassword) {
        return Owner.builder()
                .ownerEmail(this.ownerEmail)
                .ownerName(this.ownerName)
                .password(encodedPassword)
                .phoneNumber(this.phoneNumber)
                .build();
    }
}
