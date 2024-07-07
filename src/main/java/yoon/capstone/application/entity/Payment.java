package yoon.capstone.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="payment")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID")
    private long paymentIdx;

    @Column(name = "PAYMENT_CODE", nullable = false, length = 100)
    private String paymentCode;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Members members;

    @Column(name = "PAYMENT_PRODUCT", nullable = false, length = 250)
    private String product;

    @Column(name = "PAYMENT_QUANTITY", nullable = false)
    private int quantity;

    @Column(name = "PAYMENT_TOTAL")
    private int total;

    @Column(name = "PAYMENT_TID", nullable = false, length = 20)
    private String tid;//양방향 암호화

    @CreationTimestamp
    @Column(name = "PAYMENT_CREATED_AT")
    private LocalDateTime createdAt;

    @Builder
    Payment(String paymentCode, Members members, String product, int quantity, int total, String tid, LocalDateTime createdAt){
        this.paymentCode = paymentCode;
        this.members = members;
        this.product = product;
        this.quantity = quantity;
        this.total = total;
        this.tid = tid;
        this.createdAt = createdAt;
    }
}
