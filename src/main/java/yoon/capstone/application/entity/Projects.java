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

    @ManyToOne(fetch = FetchType.LAZY)
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
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROJECT_CATEGORY")
    private Categorys category;

    @Column(name = "PROJECT_GOAL_AMOUNT", nullable = false)
    private int goalAmount;

    @Column(name = "PROJECT_CURRENT_AMOUNT", nullable = false)
    private int currentAmount;

    //Query를 통한 계산 or 프로시저나 트리거 or 서비스 계층의 연산  -> p = 성능 비교 + 동시성 이슈
    @Column(name = "PROJECT_PARTICIPANTS_COUNT")
    private int participantsCount;

    @CreationTimestamp
    @Column(name = "PROJECT_CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "PROJECT_FINISH_AT")
    private LocalDateTime finishAt;

    @Column(name = "PROJECT_IS_VALID", columnDefinition = "boolean default true")
    private boolean isValid;

    @Column(name = "PROJECT_IS_HIDED", columnDefinition = "boolean default false")
    private boolean isHided;


    @Builder
    public Projects(Members members, String title, String content,String option, String link, String image, int goal, LocalDateTime finishAt, Categorys category){
        this.members = members;
        this.title = title;
        this.content = content;
        this.option = option;
        this.link = link;
        this.image = image;
        this.goalAmount = goal;
        this.currentAmount = 0;
        this.finishAt = finishAt;
        this.category = category;
    }

}
