package yoon.capstone.application.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FriendsResponse {

    private String toUser;

    private String fromUser;

    private boolean isFriends;

    private LocalDateTime redgate;

}
