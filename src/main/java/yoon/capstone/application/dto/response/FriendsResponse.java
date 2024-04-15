package yoon.capstone.application.dto.response;

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
