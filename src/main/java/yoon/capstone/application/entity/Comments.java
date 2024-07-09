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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="COMMENT_ID")
    private long commentIdx;

    @OneToOne(fetch = FetchType.LAZY)
    @Column(name = "ORDER_ID")
    private Orders orders;

    @Column(name = "COMMENT_CONTENT")
    private String content;

    @CreationTimestamp
    @Column(name = "COMMENT_CREATED_AT")
    private LocalDateTime createdAt;

    @Builder
    public Comments(Orders orders, String content){
        this.orders = orders;
        this.content = content;
    }


}
