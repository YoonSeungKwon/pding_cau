package yoon.capstone.application.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "projects")
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;

    @ManyToOne
    @JoinColumn(name = "cart_projects")
    private Carts carts;

    @Column(nullable = false, length = 250)
    private String name;

    @Column(nullable = false, length = 1000)
    private String info;

    @Column(nullable = false)
    private int goal;

    @ColumnDefault("0")
    private int curr;

    @ColumnDefault("0")
    private int count;

    @CreationTimestamp
    private LocalDateTime regdate;

    @UpdateTimestamp
    private LocalDateTime enddate;

    @ColumnDefault("1")
    private boolean isValid;

    @Builder
    public Projects(Carts carts, String name, String info, int goal, LocalDate enddate){
        this.carts = carts;
        this.name = name;
        this.info = info;
        this.goal = goal;
        this.enddate = enddate.atStartOfDay();
    }
}
