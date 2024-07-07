package yoon.capstone.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import yoon.capstone.application.enums.Categorys;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects_audit")
public class ProjectAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUDIT_ID")
    private long auditIdx;

    @Column(name = "AUDIT_PROJECT_ID")
    private long projectsIdx;

    @Column(name = "AUDIT_MEMBER_ID")
    private long membersIdx;

    @Column(name = "AUDIT_PROJECT_TITLE", length = 250)
    private String title;

    @Column(name = "AUDIT_PROJECT_CONTENT", length = 1000)
    private String content;

    @Column(name = "AUDIT_PROJECT_LINK")
    private String link;

    @Column(name = "AUDIT_PROJECT_OPTION")
    private String option;

    @Column(name = "AUDIT_PROJECT_IMAGE")
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "AUDIT_PROJECT_CATEGORY")
    private Categorys category;

    @Column(name = "AUDIT_GOAL_AMOUNT")
    private int goalAmount;

    @Column(name = "AUDIT_CURRENT_AMOUNT")
    private int currentAmount;

    //Query를 통한 계산 or 프로시저나 트리거 or 서비스 계층의 연산  -> p = 성능 비교 + 동시성 이슈
    @Column(name = "AUDIT_PROJECT_PARTICIPANTS_COUNT")
    private int participantsCount;

    @Column(name = "AUDIT_CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "AUDIT_FINISH_AT")
    private LocalDateTime finishAt;

    @CreationTimestamp
    @Column(name = "AUDIT_DELETED_AT")
    private LocalDateTime deletedAt;

    @Column(name = "AUDIT_IS_VALID")
    private boolean isValid;

    @Column(name = "AUDIT_IS_HIDED")
    private boolean isHided;

}
