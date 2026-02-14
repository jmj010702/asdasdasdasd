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
public class OrderCreateDto {
    private Long tableId;
    private UUID groupId;
    private UUID idempotencyKey;

}
