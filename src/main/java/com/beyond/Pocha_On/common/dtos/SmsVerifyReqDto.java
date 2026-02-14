package com.beyond.Pocha_On.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsVerifyReqDto {
    private String name;
    private String phone;
    private String code;
}
