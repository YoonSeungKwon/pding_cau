package yoon.capstone.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="payment", indexes = {
        @Index(name = "Payment_Index1", columnList = "PAYMENT_CODE")
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID")
    private long paymentIdx;

    @OneToOne(fetch = FetchType.EAGER)
    private Orders orders;

    @Column(name = "PAYMENT_CODE", nullable = false, length = 100)
    private String paymentCode;

    @Column(name = "PAYMENT_TOTAL")
    private int cost;

    @Column(name = "PAYMENT_TID", nullable = false, length = 20)
    private String tid;//양방향 암호화

    @CreationTimestamp
    @Column(name = "PAYMENT_CREATED_AT")
    private LocalDateTime createdAt;

    @Builder
    Payment(String paymentCode, int cost, String tid, LocalDateTime createdAt){
        this.paymentCode = paymentCode;
        this.cost = cost;
        this.tid = tid;
        this.createdAt = createdAt;
    }
}
