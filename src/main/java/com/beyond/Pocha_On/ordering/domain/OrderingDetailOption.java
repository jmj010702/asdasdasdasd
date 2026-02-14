package com.beyond.Pocha_On.ordering.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderingDetailOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderingOptionName;
    private int orderingOptionPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_detail_id", foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT), nullable = false)
    private OrderingDetail orderingDetail;
}
