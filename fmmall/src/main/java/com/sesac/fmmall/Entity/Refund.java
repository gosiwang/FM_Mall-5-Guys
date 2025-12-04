package com.sesac.fmmall.Entity;


import com.sesac.fmmall.Constant.RefundType;
import com.sesac.fmmall.Constant.YesNo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "refund")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private int id;

    @Column(name = "reason_code", length = 10, nullable = false)
    private String reasonCode;

    @Column(name = "reason_detail", length = 255, nullable = false)
    private String reasonDetail;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_type", length = 20, nullable = false)
    private RefundType refundType;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_true", length = 1, nullable = false)
    private YesNo isTrue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @OneToMany(mappedBy = "refund", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RefundItem> refundItems = new ArrayList<>();
}