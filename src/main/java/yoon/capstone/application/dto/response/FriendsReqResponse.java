package yoon.capstone.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendsReqResponse {

    private long friendIdx;

    private String email;

    private String name;

    private String profile;

    private LocalDateTime regdate;

    public FriendsReqResponse(long friendIdx, String email, String name, String profile, LocalDateTime createdAt){
        this.email = email;
        this.friendIdx = friendIdx;
        this.name = name;
        this.profile = profile;
        this.regdate = createdAt;
    }

}
