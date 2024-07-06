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
@Table(name="orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private long orderIdx;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Members members;

    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Projects projects;

    @OneToOne
    @JoinColumn(name = "PAYMENT_ID")
    private Payment payment;

    @Column(name = "ORDER_MESSAGE")
    private String message;

    @CreationTimestamp
    @Column(name = "ORDER_CREATED_AT")
    private LocalDateTime createdAt;

    @Builder
    Orders(Members members, Projects projects, Payment payment, String message){
        this.members = members;
        this.projects = projects;
        this.payment = payment;
        this.message = message;
    }

}
