package yoon.capstone.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FriendsReqResponse {

    private long friendIdx;

    private String email;

    private String name;

    private String profile;

    private boolean oauth;

    private LocalDateTime regdate;

}
