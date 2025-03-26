package yoon.capstone.application.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import yoon.capstone.application.common.dto.response.FriendsResponse;
import yoon.capstone.application.common.enums.Role;
import yoon.capstone.application.common.exception.FriendsException;
import yoon.capstone.application.common.exception.UnauthorizedException;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.config.security.JwtAuthenticationToken;
import yoon.capstone.application.infra.mock.Mock2MemberRepository;
import yoon.capstone.application.infra.mock.MockFriendRepository;
import yoon.capstone.application.infra.mock.Mock1MemberRepository;
import yoon.capstone.application.service.FriendsService;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.manager.mock.MockProfileManager;
import yoon.capstone.application.service.manager.TokenRefreshTemplate;
import yoon.capstone.application.service.manager.mock.MockAesManager;

import java.util.ArrayList;
import java.util.List;

public class FriendUnitTest {

    @Mock
    TokenRefreshTemplate tokenRefreshTemplate;

    private MemberService memberService;

    @BeforeEach
    void before(){
        this.memberService = MemberService.builder()
            .memberRepository(new Mock1MemberRepository())
            .tokenRefreshTemplate(tokenRefreshTemplate)
            .passwordEncoder(new BCryptPasswordEncoder())
            .profileManager(new MockProfileManager())
            .aesEncryptorManager(new MockAesManager())
            .build();

        JwtAuthentication jwtAuthentication = new JwtAuthentication(1, "test1@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));
    }

    Members testMember(int index){
        return Members.builder().index(index).email("test"+index+"@test.com").username("tester"+index).build();
    }

    @Test
    void 친구_친구요청_성공(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        FriendsResponse response = friendsService.requestFriends(2);

        //then
        Assertions.assertEquals(response.getToUser(), "tester2");
        Assertions.assertFalse(response.isFriends());

    }

    @Test
    void 친구_친구요청_본인(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        JwtAuthentication jwtAuthentication = new JwtAuthentication(2, "test1@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        //then
        Assertions.assertThrows(FriendsException.class, ()->friendsService.requestFriends(2));


    }

    @Test
    void 친구_친구요청_이미친구(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        FriendsResponse response1 = friendsService.requestFriends(2);

        //then
        Assertions.assertThrows(FriendsException.class, ()->friendsService.requestFriends(2));

    }

    @Test
    void 친구_요청수락_성공(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        FriendsResponse response1 = friendsService.requestFriends(2);

        JwtAuthentication jwtAuthentication = new JwtAuthentication(2, "test2@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        FriendsResponse response = friendsService.acceptFriends(response1.getFriendIdx()).get(0);
        //then

        Assertions.assertTrue(response.isFriends());

    }

    @Test
    void 친구_요청수락_이미친구(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        FriendsResponse response1 = friendsService.requestFriends(2);

        JwtAuthentication jwtAuthentication = new JwtAuthentication(2, "test2@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));
        friendsService.acceptFriends(response1.getFriendIdx());
        //then
        Assertions.assertThrows(FriendsException.class, ()->friendsService.acceptFriends(response1.getFriendIdx()));

    }

    @Test
    void 친구_요청수락_존재하지않음(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        JwtAuthentication jwtAuthentication = new JwtAuthentication(2, "test2@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        //then
        Assertions.assertThrows(FriendsException.class, ()->friendsService.acceptFriends(0));
    }

    @Test
    void 친구_요청거절_성공(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        FriendsResponse response1 = friendsService.requestFriends(2);

        JwtAuthentication jwtAuthentication = new JwtAuthentication(2, "test2@test.com?", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        friendsService.declineFriends(response1.getFriendIdx());
        //then

        Assertions.assertEquals(friendsService.getFriendsList().size(), 0);


    }

    @Test
    void 친구_요청거절_존재하지않음(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        JwtAuthentication jwtAuthentication = new JwtAuthentication(2, "test2@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        //then

        Assertions.assertThrows(FriendsException.class, ()->friendsService.declineFriends(0));

    }

    @Test
    void 친구_요청거절_권한없음(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        FriendsResponse response1 = friendsService.requestFriends(2);

        JwtAuthentication jwtAuthentication = new JwtAuthentication(3, "test3@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        //then

        Assertions.assertThrows(UnauthorizedException.class, ()->friendsService.declineFriends(response1.getFriendIdx()));


    }

    @Test
    void 친구_삭제_존재하지않음() {
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        JwtAuthentication jwtAuthentication = new JwtAuthentication(3, "test3@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        //then
        Assertions.assertThrows(FriendsException.class, ()->friendsService.deleteFriends(3));

    }

    @Test
    void 친구_삭제_성공() {
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        FriendsResponse response1 = friendsService.requestFriends(2);

        JwtAuthentication jwtAuthentication = new JwtAuthentication(2, "test2@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        friendsService.acceptFriends(0);
        friendsService.deleteFriends(0);
        friendsService.deleteFriends(1);

        //then
        Assertions.assertEquals(friendsService.getFriendsList().size(), 0);
    }

    @Test
    void 친구_목록불러오기_성공(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        friendsService.requestFriends(2);
        friendsService.requestFriends(3);
        friendsService.requestFriends(4);
        friendsService.requestFriends(5);
        friendsService.requestFriends(6);

        //then
        Assertions.assertEquals(friendsService.getFriendsList().size(), 5);

    }

    @Test
    void 친구_요청불러오기_성공(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Mock2MemberRepository())
                .friendsRepository(new MockFriendRepository())
                .aesEncryptorManager(new MockAesManager())
                .build();


        //when
        friendsService.requestFriends(6);
        JwtAuthentication jwtAuthentication1 = new JwtAuthentication(2, "test2@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication1, null, new ArrayList<>()));
        friendsService.requestFriends(6);
        JwtAuthentication jwtAuthentication2 = new JwtAuthentication(3, "test3@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication2, null, new ArrayList<>()));
        friendsService.requestFriends(6);
        JwtAuthentication jwtAuthentication3 = new JwtAuthentication(4, "test4@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication3, null, new ArrayList<>()));
        friendsService.requestFriends(6);
        JwtAuthentication jwtAuthentication4 = new JwtAuthentication(5, "test5@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication4, null, new ArrayList<>()));
        friendsService.requestFriends(6);

        JwtAuthentication jwtAuthentication = new JwtAuthentication(6, "test6@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        //then
        Assertions.assertEquals(friendsService.getFriendsRequest().size(), 5);


    }

}
