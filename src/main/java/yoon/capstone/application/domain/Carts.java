package yoon.capstone.application.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart")
public class Carts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;

    @CreationTimestamp
    private LocalDateTime regdate;

}
