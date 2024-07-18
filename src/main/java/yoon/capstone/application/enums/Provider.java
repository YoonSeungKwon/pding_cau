package yoon.capstone.application.enums;

import lombok.Getter;

@Getter
public enum Provider {

    GOOGLE("GOOGLE"),
    NAVER("NAVER"),
    KAKAO("KAKAO"),
    DEFAULT("DEFAULT");

    private final String provider;

    Provider(String provider){
        this.provider = provider;
    }


}