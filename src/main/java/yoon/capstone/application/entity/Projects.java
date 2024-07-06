package yoon.capstone.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
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
    @Column(name = "PROJECT_ID")
    private long projectIdx;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Members members;

    @Column(name = "PROJECT_TITLE", nullable = false, length = 250)
    private String title;

    @Column(name = "PROJECT_CONTENT", nullable = false, length = 1000)
    private String content;

    @Column(name = "PROJECT_LINK")
    private String link;

    @Column(name = "PROJECT_OPTION")
    private String option;

    @Column(name = "PROJECT_IMAGE")
    private String img;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROJECT_CATEGORY")
    private Categorys category;

    @Column(name = "GOAL_AMOUNT", nullable = false)
    private int goalAmount;

    @Column(name = "CURRENT_AMOUNT", nullable = false)
    private int currentAmount;

    //Query 참여자 수
    private int count;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "FINISH_AT")
    private LocalDateTime finishAt;

    @ColumnDefault("1")
    @Column(name = "IS_VALID")
    private boolean isValid;

    @ColumnDefault("0")
    @Column(name = "IS_HIDED")
    private boolean isHided;


    @Builder
    public Projects(Members members, String title, String content,String option, String link, String img, int goal, LocalDate finishAt, Categorys category){
        this.members = members;
        this.title = title;
        this.content = content;
        this.option = option;
        this.link = link;
        this.img = img;
        this.goalAmount = goal;
        this.currentAmount = 0;
        this.finishAt = finishAt.atStartOfDay();
        this.category = category;
    }

}
