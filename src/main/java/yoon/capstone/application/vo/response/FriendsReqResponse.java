package yoon.capstone.application.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FriendsReqResponse {

    private String email;

    private String name;

    private String profile;

    private boolean oauth;

    private LocalDateTime regdate;

}
