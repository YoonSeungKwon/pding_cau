package yoon.capstone.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="friends", indexes = {
        @Index(name = "OX_FRIEND", columnList = "FRIEND_FROM_USER, FRIEND_TO_USER")
})
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRIEND_ID")
    private long friendIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FRIEND_TO_USER")
    private Members toUser;

    @Column(name = "FRIEND_FROM_USER", nullable = false, length = 1)
    private long fromUser;

    @Column(name = "IS_FRIEND", columnDefinition = "boolean default false")
    private boolean isFriends;

    @CreationTimestamp
    @Column(name = "FRIEND_CREATED_AT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "FRIEND_UPDATED_AT")
    private LocalDateTime updatedAt;


    @Builder
    Friends(Members toUser, long fromUser){
        this.toUser = toUser;
        this.fromUser = fromUser;
    }
}
