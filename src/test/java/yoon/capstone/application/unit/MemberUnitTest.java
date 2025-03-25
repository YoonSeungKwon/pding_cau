package yoon.capstone.application.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import yoon.capstone.application.common.dto.request.LoginDto;
import yoon.capstone.application.common.dto.request.RegisterDto;
import yoon.capstone.application.common.dto.response.MemberResponse;
import yoon.capstone.application.common.enums.Role;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.config.security.JwtAuthenticationToken;
import yoon.capstone.application.infra.mock.Mock1MemberRepository;
import yoon.capstone.application.presentation.MemberController;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.manager.mock.MockProfileManager;
import yoon.capstone.application.service.manager.RefreshTemplate;
import yoon.capstone.application.service.manager.mock.MockRefreshTemplate;
import yoon.capstone.application.service.manager.mock.MockAesManager;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.Random;

public class MemberUnitTest {

    /**
     멤버 테스트
     **/
    @Mock
    RefreshTemplate tokenRefreshTemplate;
    @Test
    void 멤버_이메일중복체크_중복없음(){
        //given
        MemberService memberService = MemberService.builder()
                .memberRepository(new Mock1MemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new MockAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        Random random = new Random();

        //when
        String email = "test"+random.nextInt(0, 10000)+"@test.com";

        //then
        Assertions.assertFalse(memberService.existUser(email));
    }

    @Test
    void 멤버_회원가입_성공(){
        //given
        MemberService memberService = MemberService.builder()
                .memberRepository(new Mock1MemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new MockAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        Random random = new Random();

        String email = "test"+random.nextInt(0, 10000)+"@test.com";
        String password = "test1234"+random.nextInt(0, 10000);
        String username = "tester"+random.nextInt(0, 10000);
        String phone = "010-1234-5678";

        //when
        MemberResponse response = memberService.formRegister(new RegisterDto(email, password, username, phone));

        //then
        Assertions.assertEquals(response.getEmail(), email);
        Assertions.assertEquals(response.getName(), username);
        Assertions.assertEquals(response.getPhone(), phone);
    }
    @Test
    void 멤버_회원가입_이메일중복(){
        //given
        MemberService memberService = MemberService.builder()
                .memberRepository(new Mock1MemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new MockAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        Random random = new Random();

        String email = "test"+random.nextInt(0, 10000)+"@test.com";
        String password = "test1234"+random.nextInt(0, 10000);
        String username = "tester"+random.nextInt(0, 10000);
        String phone = "010-1234-5678";

        //when
        memberService.formRegister(new RegisterDto(email, password, username, phone));

        //then
        Assertions.assertTrue(memberService.existUser(email));
    }
    @Test
    void 멤버_회원가입_AES인코딩(){
        //given
        MemberService memberService = MemberService.builder()
                .memberRepository(new Mock1MemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new MockAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        //when
        Random random = new Random();

        String email = "test"+random.nextInt(0, 10000)+"@test.com";
        String password = "test1234"+random.nextInt(0, 10000);
        String username = "tester"+random.nextInt(0, 10000);
        String phone = "010-1234-5678";

        MemberResponse memberResponse = memberService.formRegister(new RegisterDto(email, password, username, phone));

        //then
        Assertions.assertEquals(phone, memberResponse.getPhone());
    }

    @Test
    void 멤버_로그인_이메일존재하지않음(){
        //given
        MemberService memberService = MemberService.builder()
                .memberRepository(new Mock1MemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new MockAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        //when
        LoginDto dto = new LoginDto("test@test.com", "12345678");
        MockHttpServletResponse response = new MockHttpServletResponse();


        //then
        Assertions.assertThrows(UsernameNotFoundException.class, ()->memberService.formLogin(dto, response));

    }

    @Test
    void 멤버_로그인_비밀번호일치하지않음(){
        //given
        MemberService memberService = MemberService.builder()
                .memberRepository(new Mock1MemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new MockAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        //when
        RegisterDto dto = new RegisterDto("test@test.com", "12345678", "tester", "010-1234-5678");
        memberService.formRegister(dto);

        LoginDto loginDto = new LoginDto("test@test.com", "123456789");
        MockHttpServletResponse response = new MockHttpServletResponse();


        //then
        Assertions.assertThrows(BadCredentialsException.class, ()->memberService.formLogin(loginDto, response));

    }

    @Test
    void 멤버_로그인_성공(){
        //given
        MemberService memberService = MemberService.builder()
                .memberRepository(new Mock1MemberRepository())
                .tokenRefreshTemplate(new MockRefreshTemplate())
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new MockAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        //when
        RegisterDto dto = new RegisterDto("test@test.com", "12345678", "tester", "010-1234-5678");
        memberService.formRegister(dto);

        LoginDto loginDto = new LoginDto("test@test.com", "12345678");
        MockHttpServletResponse response = new MockHttpServletResponse();

        MemberResponse result = memberService.formLogin(loginDto, response);

        //then
        Assertions.assertEquals(result.getPhone(), "010-1234-5678");
        Assertions.assertEquals(result.getName(), "tester");
        Assertions.assertEquals(result.getEmail(), "test@test.com");
        Assertions.assertEquals(result.getMemberIdx(), 0);
    }

    @Test
    void 멤버_로그아웃_토큰시도(){
        //given
        MemberService memberService = MemberService.builder()
                .memberRepository(new Mock1MemberRepository())
                .tokenRefreshTemplate(new MockRefreshTemplate())
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new MockAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        //when
        RegisterDto dto = new RegisterDto("test@test.com", "12345678", "tester", "010-1234-5678");
        memberService.formRegister(dto);

        LoginDto loginDto = new LoginDto("test@test.com", "12345678");
        MockHttpServletResponse response = new MockHttpServletResponse();

        MemberResponse result = memberService.formLogin(loginDto, response);

        JwtAuthentication jwtAuthentication = new JwtAuthentication(0, "test@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        //then
        Assertions.assertNull(memberService.logOut().getRefreshToken());
    }

    @Test
    void 멤버_프로필변경_성공(){
        //given
        MemberService memberService = MemberService.builder()
                .memberRepository(new Mock1MemberRepository())
                .tokenRefreshTemplate(new MockRefreshTemplate())
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new MockAesManager())
                .passwordEncoder(new BCryptPasswordEncoder())
                .build();

        //when
        RegisterDto dto = new RegisterDto("test@test.com", "12345678", "tester", "010-1234-5678");
        memberService.formRegister(dto);

        JwtAuthentication jwtAuthentication = new JwtAuthentication(0, "test@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        MemberResponse result = memberService.uploadProfile(new MockMultipartFile("file", new byte[]{}));

        //then
        Assertions.assertEquals(result.getProfile(), "file");
    }



}
