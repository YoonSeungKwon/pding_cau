package yoon.capstone.application.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.domain.Members;

public class MemberUnitTest {

    /**
     회원가입 테스트
     **/
    @Test
    void 회원가입_성공_테스트(){

        MemberService memberService = MemberService.builder()
                .profileManager()
                .memberRepository()
                .aesEncryptorManager()
                .tokenRefreshTemplate()
                .build();



        Assertions.assertEquals();
    }
    @Test
    void 회원가입_이메일_중복_테스트(){

    }
    @Test
    void 회원가입_이메일_체크_테스트(){

    }
    @Test
    void 회원가입_AES_인코딩_테스트(){

    }

    /**
     로그인 테스트
     **/
    @Test
    void 로그인_토큰_테스트(){

    }




}
