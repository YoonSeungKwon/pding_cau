package yoon.capstone.application.enums;

import lombok.Getter;

@Getter
public enum Provider {

    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao"),
    NULL("null");

    private final String provider;

    Provider(String provider){
        this.provider = provider;
    }


}