package com.beyond.Pocha_On.customerorder.dto;


import com.beyond.Pocha_On.ordering.domain.OrderingDetailOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderListDetailOpDto {
    private Long optionId;

    public static OrderListDetailOpDto fromEntity(OrderingDetailOption option){
        return OrderListDetailOpDto.builder()
                .optionId(option.getId())
                .build();


    }
}
