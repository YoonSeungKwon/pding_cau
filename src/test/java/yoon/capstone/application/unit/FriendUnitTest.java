package yoon.capstone.application.unit;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import yoon.capstone.application.infra.stub.StubFriendRepository;
import yoon.capstone.application.infra.stub.StubMemberRepository;
import yoon.capstone.application.service.FriendsService;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.manager.MockProfileManager;
import yoon.capstone.application.service.manager.TokenRefreshTemplate;
import yoon.capstone.application.service.stub.StubAesManager;

public class FriendUnitTest {

    @Mock
    TokenRefreshTemplate tokenRefreshTemplate;

    MemberService memberService = MemberService.builder()
            .memberRepository(new StubMemberRepository())
            .tokenRefreshTemplate(tokenRefreshTemplate)
            .passwordEncoder(new BCryptPasswordEncoder())
            .profileManager(new MockProfileManager())
            .aesEncryptorManager(new StubAesManager())
            .build();

    @Test
    void 친구_요청(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new StubMemberRepository())
                .friendsRepository(new StubFriendRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();


        //when


        //then


    }

    @Test
    void 친구_요청_중복(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new StubMemberRepository())
                .friendsRepository(new StubFriendRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();


        //when


        //then


    }

    @Test
    void 친구_요청_수락(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new StubMemberRepository())
                .friendsRepository(new StubFriendRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();


        //when


        //then


    }

    @Test
    void 친구_요청_거절(){
        //given
        FriendsService friendsService = FriendsService.builder()
                .memberRepository(new StubMemberRepository())
                .friendsRepository(new StubFriendRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();


        //when


        //then


    }

}
