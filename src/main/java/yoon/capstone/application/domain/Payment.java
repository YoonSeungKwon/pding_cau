package yoon.capstone.application.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="payment")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;

    @Column(nullable = false, length = 100)
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "member_payment")
    private Members members;

    @Column(nullable = false, length = 250)
    private String itemName;

    private int quantity;

    private int total;

    @Column(nullable = false, length = 20)
    private String tid;

    private LocalDateTime regdate;

    @Builder
    Payment(String orderId, Members members, String itemName, int quantity, int total, String tid, LocalDateTime regdate){
        this.orderId = orderId;
        this.members = members;
        this.itemName = itemName;
        this.quantity = quantity;
        this.total = total;
        this.tid = tid;
        this.regdate = regdate;
    }
}
