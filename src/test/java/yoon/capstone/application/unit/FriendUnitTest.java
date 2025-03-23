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
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.config.security.JwtAuthenticationToken;
import yoon.capstone.application.infra.stub.Stub1MemberRepository;
import yoon.capstone.application.infra.stub.StubFriendRepository;
import yoon.capstone.application.infra.stub.StubMemberRepository;
import yoon.capstone.application.service.FriendsService;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.manager.MockProfileManager;
import yoon.capstone.application.service.manager.TokenRefreshTemplate;
import yoon.capstone.application.service.manager.stub.StubAesManager;

import java.util.ArrayList;

public class FriendUnitTest {

    @Mock
    TokenRefreshTemplate tokenRefreshTemplate;

    private MemberService memberService;

    @BeforeEach
    void before(){
        this.memberService = MemberService.builder()
            .memberRepository(new StubMemberRepository())
            .tokenRefreshTemplate(tokenRefreshTemplate)
            .passwordEncoder(new BCryptPasswordEncoder())
            .profileManager(new MockProfileManager())
            .aesEncryptorManager(new StubAesManager())
            .build();

        JwtAuthentication jwtAuthentication = new JwtAuthentication(1, "test1@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));
    }

    Members testMember(int index){
        return Members.builder().index(index).email("test"+index+"@test.com").username("tester"+index).build();
    }

    @Test
    void 친구_요청(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Stub1MemberRepository())
                .friendsRepository(new StubFriendRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();


        //when
        FriendsResponse response = friendsService.requestFriends(2);

        //then
        Assertions.assertEquals(response.getToUser(), "tester2");
        Assertions.assertFalse(response.isFriends());

    }

    @Test
    void 본인에게_친구_요청(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Stub1MemberRepository())
                .friendsRepository(new StubFriendRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();


        //when
        JwtAuthentication jwtAuthentication = new JwtAuthentication(2, "test1@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        //then
        Assertions.assertThrows(FriendsException.class, ()->friendsService.requestFriends(2));


    }

    @Test
    void 친구_요청_중복(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Stub1MemberRepository())
                .friendsRepository(new StubFriendRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();


        //when
        FriendsResponse response1 = friendsService.requestFriends(2);

        //then
        Assertions.assertThrows(FriendsException.class, ()->friendsService.requestFriends(2));

    }

    @Test
    void 친구_요청_수락(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Stub1MemberRepository())
                .friendsRepository(new StubFriendRepository())
                .aesEncryptorManager(new StubAesManager())
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
    void 친구_요청_거절(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new Stub1MemberRepository())
                .friendsRepository(new StubFriendRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();


        //when
        FriendsResponse response1 = friendsService.requestFriends(2);

        JwtAuthentication jwtAuthentication = new JwtAuthentication(2, "test2@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));

        friendsService.declineFriends(response1.getFriendIdx());
        //then

        Assertions.assertEquals(friendsService.getFriendsList().size(), 0);


    }

}
