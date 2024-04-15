package yoon.capstone.application.dto.request;

import lombok.Getter;

@Getter
public class FriendsDto {

    private String toUserEmail;

    private String fromUserEmail;

    private boolean oauth;
}
