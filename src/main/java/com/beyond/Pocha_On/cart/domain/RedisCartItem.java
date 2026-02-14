package com.beyond.Pocha_On.cart.domain;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder

public class RedisCartItem {
    private Long menuId;
    private String optionKey; //편의
    private int quantity;
    private int unitPrice;

    public void setQuantity(int quantity){
        this.quantity =quantity;
    }


}