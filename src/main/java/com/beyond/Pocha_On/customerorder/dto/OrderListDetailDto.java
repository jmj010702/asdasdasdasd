package com.beyond.Pocha_On.customerorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderListDetailDto {
    private Long menuId;
    private int menuQuantity;
    private int linePrice;
    private List<OrderListDetailOpDto> orderDetailOpDto;

    }


