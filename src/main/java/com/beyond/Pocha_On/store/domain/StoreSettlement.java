package com.beyond.Pocha_On.store.domain;

import com.beyond.Pocha_On.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
public class StoreSettlement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeSettlementId; // 정산 저장 테이블

    private Long dayTotalAmount; //일일 매출

    @ManyToOne
    @JoinColumn(nullable = false)
    private Store store;



}
