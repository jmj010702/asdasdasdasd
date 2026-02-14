package com.beyond.Pocha_On.cart.dto.cart_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartLineDeleteDto {
    private Long tableId;
    private Long menuId;
    private List<Long> optionIds;
}
