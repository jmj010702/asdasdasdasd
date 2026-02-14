package com.beyond.Pocha_On.cart.dto.cart_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartCreateDto {
    private Long tableId;
    private List<CartCreateDetailDto> createDetailDtos;
}
