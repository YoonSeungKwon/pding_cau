package yoon.capstone.application.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import yoon.capstone.application.enums.Categorys;

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
    @JoinColumn(name = "project_members")
    private Members members;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private int goal;

    @ColumnDefault("0")
    private int curr;

    @CreationTimestamp
    private LocalDateTime regdate;

    private LocalDateTime enddate;

    private String link;

    private String option;

    @ColumnDefault("0")
    private int count;

    @ColumnDefault("1")
    private boolean isValid;

    @ColumnDefault("0")
    private boolean isHided;

    private String img;

    @Enumerated(EnumType.STRING)
    private Categorys category;


    @Builder
    public Projects(Members members, String title, String content,String option, String link, String img, int goal, LocalDate enddate, Categorys category){
        this.members = members;
        this.title = title;
        this.content = content;
        this.option = option;
        this.link = link;
        this.img = img;
        this.goal = goal;
        this.enddate = enddate.atStartOfDay();
        this.category = category;
    }
}
