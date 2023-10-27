package yoon.capstone.application.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendsResponse {

    private String toUser;

    private String fromUser;

    private boolean isFriends;

}
