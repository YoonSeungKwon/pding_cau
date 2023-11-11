package yoon.capstone.application.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
//보류
//@Entity
//@Table(name = "carts")
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class Carts {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long idx;
//
//    @UpdateTimestamp
//    private LocalDateTime updated;
//
//    @OneToOne
//    @JoinColumn(name = "members_cart")
//    private Members members;
//
//    @Builder
//    public Carts(Members members){
//        this.members = members;
//    }
//}
