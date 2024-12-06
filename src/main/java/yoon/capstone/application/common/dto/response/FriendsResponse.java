package yoon.capstone.application.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FriendsResponse {

    private long friendIdx;

    private String toUser;

    private boolean isFriends;

    private LocalDateTime redgate;

}
