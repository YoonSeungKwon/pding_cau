package yoon.capstone.application.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import yoon.capstone.application.common.dto.request.ProjectDto;
import yoon.capstone.application.common.enums.Role;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.config.security.JwtAuthenticationToken;
import yoon.capstone.application.infra.stub.Stub1MemberRepository;
import yoon.capstone.application.infra.stub.StubFriendRepository;
import yoon.capstone.application.infra.stub.StubMemberRepository;
import yoon.capstone.application.infra.stub.StubProjectRepository;
import yoon.capstone.application.service.FriendsService;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.ProjectService;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.manager.MockProfileManager;
import yoon.capstone.application.service.manager.ProfileManager;
import yoon.capstone.application.service.manager.TokenRefreshTemplate;
import yoon.capstone.application.service.manager.stub.StubAesManager;

import java.util.ArrayList;

public class ProjectUnitTest {

    private MemberService memberService;

    private FriendsService friendsService;

    private TokenRefreshTemplate tokenRefreshTemplate;

    private ProfileManager profileManager;



    @BeforeEach
    void before(){
        this.memberService = MemberService.builder()
                .memberRepository(new StubMemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .passwordEncoder(new BCryptPasswordEncoder())
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new StubAesManager())
                .build();

        this.friendsService = FriendsService.builder()
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new Stub1MemberRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();

        JwtAuthentication jwtAuthentication = new JwtAuthentication(1, "test1@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));
    }

    Members testMember(int index){
        return Members.builder().index(index).email("test"+index+"@test.com").username("tester"+index).build();
    }

    @Test
    void 프로젝트_생성_테스트(){
        //given
        ProjectService projectService = ProjectService.builder()
                .projectsRepository(new StubProjectRepository())
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new StubMemberRepository())
                .profileManager(this.profileManager)
                .build();

        //when

        projectService.makeProjects(null, new ProjectDto());

        //then

    }


}
