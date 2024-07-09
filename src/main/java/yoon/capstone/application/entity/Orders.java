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
    @JoinColumn(name = "MEMBER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Members members;

    @ManyToOne
    @JoinColumn(name = "PROJECT_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Projects projects;

    @OneToOne(mappedBy = "orders", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "PAYMENT_ID")
    private Payment payment;

    @OneToOne(mappedBy = "orders", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "COMMENT_ID")
    private Comments comments;

    @CreationTimestamp
    @Column(name = "ORDER_CREATED_AT")
    private LocalDateTime createdAt;

    @Builder
    Orders(Members members, Projects projects, Payment payment){
        this.members = members;
        this.projects = projects;
        this.payment = payment;
    }

}
