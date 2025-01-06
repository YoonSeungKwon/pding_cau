package yoon.capstone.application.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import yoon.capstone.application.common.dto.request.RegisterDto;
import yoon.capstone.application.common.dto.response.MemberResponse;
import yoon.capstone.application.infra.stub.StubMemberRepository;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.manager.MockProfileManager;
import yoon.capstone.application.service.manager.TokenRefreshTemplate;
import yoon.capstone.application.service.stub.StubAesManager;

import java.util.Random;

public class MemberUnitTest {

    /**
     회원가입 테스트
     **/
    @Mock
    TokenRefreshTemplate tokenRefreshTemplate;

    @Test
    void 기본_회원가입_성공(){

        MemberService memberService = MemberService.builder()
                .memberRepository(new StubMemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new StubAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
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
    void 회원가입_이메일_중복가입(){
        MemberService memberService = MemberService.builder()
                .memberRepository(new StubMemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new StubAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
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
    void 회원가입_이메일_중복체크(){
        MemberService memberService = MemberService.builder()
                .memberRepository(new StubMemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new StubAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
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
    void 회원가입_AES_인코딩(){
        MemberService memberService = MemberService.builder()
                .memberRepository(new StubMemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new StubAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        Random random = new Random();

        String email = "test"+random.nextInt(0, 10000)+"@test.com";
        String password = "test1234"+random.nextInt(0, 10000);
        String username = "tester"+random.nextInt(0, 10000);
        String phone = "010-1234-5678";

        MemberResponse memberResponse = memberService.formRegister(new RegisterDto(email, password, username, phone));

        Assertions.assertEquals(phone, memberResponse.getPhone());
    }

    /**
     로그인 테스트
     **/
    @Test
    void login_jwt(){

    }




}
