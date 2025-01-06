package yoon.capstone.application.unit;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import yoon.capstone.application.infra.stub.StubMemberRepository;
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
    void 친구_등록(){

    }

}
