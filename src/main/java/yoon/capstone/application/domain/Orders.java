package yoon.capstone.application.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;

    @ManyToOne
    @JoinColumn(name = "orders_member")
    private Members members;

    @ManyToOne
    @JoinColumn(name = "orders_project")
    private Projects projects;

    @OneToOne
    @JoinColumn(name = "orders_payment")
    private Payment payment;

    @Builder
    Orders(Members members, Projects projects, Payment payment){
        this.members = members;
        this.projects = projects;
        this.payment = payment;
    }

}
