package com.beyond.Pocha_On.ordering.domain;

import com.beyond.Pocha_On.common.BaseTimeEntity;
import com.beyond.Pocha_On.customerTable.domain.CustomerTable;
import com.beyond.Pocha_On.pay.domain.PaymentState;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Ordering extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT), nullable = false)
    private CustomerTable customerTable;

    @Column(columnDefinition = "BINARY(16)")
    private UUID groupId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentState paymentState = PaymentState.PENDING;

    //    멱등성 추가
    @Column(name = "idempotency_key", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID idempotencyKey;

    //    주문 상태 : CANCELLED, STANDBY(주문이 왔을때) , DONE(완료 버튼 눌렀을 때)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.STANDBY;

    @OneToMany(mappedBy = "ordering", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderingDetail> orderDetail = new ArrayList<>();

    public void updatePaymentState(PaymentState paymentState) {
        this.paymentState = paymentState;
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    public void setTotalPrice(int price){
        this.totalPrice =price;
    }
}
