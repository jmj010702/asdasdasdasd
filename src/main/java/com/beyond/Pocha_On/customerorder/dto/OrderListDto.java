package com.beyond.Pocha_On.customerorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderListDto {
    private Long tableId;
    private UUID groupId;
    private int totalPrice;
    private List<OrderListDetailDto> listDetailDto;
}
