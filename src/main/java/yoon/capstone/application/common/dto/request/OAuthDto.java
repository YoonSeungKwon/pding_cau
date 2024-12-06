package yoon.capstone.application.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthDto {

    private String email;

    private String name;

    private String image;
}
