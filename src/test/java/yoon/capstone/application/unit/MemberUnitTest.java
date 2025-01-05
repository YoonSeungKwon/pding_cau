package yoon.capstone.application.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import yoon.capstone.application.common.dto.request.RegisterDto;
import yoon.capstone.application.common.dto.response.MemberResponse;
import yoon.capstone.application.common.util.AesEncryptorManager;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.manager.MockProfileManager;
import yoon.capstone.application.service.manager.TokenRefreshTemplate;
import yoon.capstone.application.service.repository.MemberRepository;

import java.util.Random;

@SpringBootTest
public class MemberUnitTest {

    /**
     회원가입 테스트
     **/
    @Autowired
    AesEncryptorManager aesEncryptorManager;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TokenRefreshTemplate tokenRefreshTemplate;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Test
    void member_register_success(){

        MemberService memberService = MemberService.builder()
                .memberRepository(memberRepository)
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(aesEncryptorManager)
                .passwordEncoder(passwordEncoder)
                .build();

        Random random = new Random();

        String email = "test"+random.nextInt(0, 10000)+"@test.com";
        String password = "test1234"+random.nextInt(0, 10000);
        String username = "tester"+random.nextInt(0, 10000);
        String phone = "010-1234-5678";

        MemberResponse response = memberService.formRegister(new RegisterDto(email, password, username, phone));

        Assertions.assertEquals(response.getEmail(), email);
        Assertions.assertEquals(response.getName(), username);
        Assertions.assertEquals(response.getPhone(), phone);

    }
    @Test
    void register_email_duplicate(){
        MemberService memberService = MemberService.builder()
                .memberRepository(memberRepository)
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(aesEncryptorManager)
                .passwordEncoder(passwordEncoder)
                .build();

        Random random = new Random();

        String email = "test"+random.nextInt(0, 10000)+"@test.com";
        String password = "test1234"+random.nextInt(0, 10000);
        String username = "tester"+random.nextInt(0, 10000);
        String phone = "010-1234-5678";

        memberService.formRegister(new RegisterDto(email, password, username, phone));

        Assertions.assertTrue(memberService.existUser(email));
    }
    @Test
    void register_email_check(){
        MemberService memberService = MemberService.builder()
                .memberRepository(memberRepository)
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(aesEncryptorManager)
                .passwordEncoder(passwordEncoder)
                .build();

        Random random = new Random();

        String email = "test"+random.nextInt(0, 10000)+"@test.com";
        String password = "test1234"+random.nextInt(0, 10000);
        String username = "tester"+random.nextInt(0, 10000);
        String phone = "010-1234-5678";

        memberService.formRegister(new RegisterDto(email, password, username, phone));

        Assertions.assertTrue(memberService.existUser(email));
    }
    @Test
    void register_aes_encode(){
        MemberService memberService = MemberService.builder()
                .memberRepository(memberRepository)
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(aesEncryptorManager)
                .passwordEncoder(passwordEncoder)
                .build();

        Random random = new Random();

        String email = "test"+random.nextInt(0, 10000)+"@test.com";
        String password = "test1234"+random.nextInt(0, 10000);
        String username = "tester"+random.nextInt(0, 10000);
        String phone = "010-1234-5678";

        memberService.formRegister(new RegisterDto(email, password, username, phone));

        Assertions.assertEquals(memberService.findMember(email).get(0).getPhone(), phone);
    }

    /**
     로그인 테스트
     **/
    @Test
    void login_jwt(){

    }




}
