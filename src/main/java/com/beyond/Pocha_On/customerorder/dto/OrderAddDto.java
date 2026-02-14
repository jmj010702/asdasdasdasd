package com.beyond.Pocha_On.customerorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderAddDto {
    private UUID groupId;
    private Long tableId;
    private UUID idempotencyKey;

}
