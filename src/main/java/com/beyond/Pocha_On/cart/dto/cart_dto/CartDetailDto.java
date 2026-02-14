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
public class CartDetailDto {
    private Long menuId;
    private String menuName;
    private int lineTotalPrice; //매뉴*수량+옵션값
    private int menuQuantity;
    private List<CartOptionDto> cartOptionDtos;
}


