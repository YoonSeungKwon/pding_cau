package yoon.capstone.application.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import yoon.capstone.application.common.dto.request.ProjectDto;
import yoon.capstone.application.common.dto.response.ProjectDetailResponse;
import yoon.capstone.application.common.dto.response.ProjectResponse;
import yoon.capstone.application.common.enums.Category;
import yoon.capstone.application.common.enums.Role;
import yoon.capstone.application.common.exception.FriendsException;
import yoon.capstone.application.common.exception.UnauthorizedException;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.config.security.JwtAuthenticationToken;
import yoon.capstone.application.infra.stub.*;
import yoon.capstone.application.service.FriendsService;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.ProjectService;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.service.manager.MockProfileManager;
import yoon.capstone.application.service.manager.ProfileManager;
import yoon.capstone.application.service.manager.TokenRefreshTemplate;
import yoon.capstone.application.service.manager.stub.StubAesManager;
import yoon.capstone.application.service.manager.stub.StubProfileManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectUnitTest {

    private MemberService memberService;

    private FriendsService friendsService;

    private TokenRefreshTemplate tokenRefreshTemplate;

    private ProfileManager profileManager;



    @BeforeEach
    void before(){
        this.memberService = MemberService.builder()
                .memberRepository(new Stub1MemberRepository())
                .tokenRefreshTemplate(tokenRefreshTemplate)
                .passwordEncoder(new BCryptPasswordEncoder())
                .profileManager(new MockProfileManager())
                .aesEncryptorManager(new StubAesManager())
                .build();

        this.friendsService = FriendsService.builder()
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new Stub2MemberRepository())
                .aesEncryptorManager(new StubAesManager())
                .build();

        JwtAuthentication jwtAuthentication = new JwtAuthentication(1, "test1@test.com", null, Role.USER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtAuthentication, null, new ArrayList<>()));
    }

    Members makeMember(int index){
        return Members.builder().index(index).email("test"+index+"@test.com").username("tester"+index).build();
    }

    Projects makeProject(int index){
        return Projects.builder().index(index).goal(index*10000).title("test"+index).content("test project").category(Category.생일).finishAt(LocalDateTime.now())
                .link("test"+index+".com").build();
    }

    @Test
    void 프로젝트_생성_성공(){
        //given
        ProjectService projectService = ProjectService.builder()
                .projectsRepository(new StubProjectRepository())
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new Stub1MemberRepository())
                .profileManager(new StubProfileManager())
                .build();

        //when
        List<ProjectResponse> result = projectService.makeProjects(null, new ProjectDto("test", "test", "생일", "", "test", 10000
                , LocalDateTime.now()));

        //then
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0).getCurr(), 0);
        Assertions.assertEquals(result.get(0).getCount(), 0);
        Assertions.assertEquals(result.get(0).getTitle(), "test");
        Assertions.assertEquals(result.get(0).getGoal(), 10000);
        Assertions.assertEquals(result.get(0).getWriter(), "tester1");
        Assertions.assertEquals(result.get(0).getIdx(), 0);
    }

    @Test
    void 프로젝트_유저인증_실패(){
        //given
        ProjectService projectService = ProjectService.builder()
                .projectsRepository(new StubProjectRepository())
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new Stub1MemberRepository())
                .profileManager(new StubProfileManager())
                .build();

        //when
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(new JwtAuthentication(2, "", "", Role.USER), null, new ArrayList<>()));

        //then
        Assertions.assertThrows(UnauthorizedException.class, ()->projectService.makeProjects(null, new ProjectDto("test", "test", "생일", "", "test", 10000
                , LocalDateTime.now())));

    }

    @Test
    void 프로젝트_카테고리_미존재(){
        //given
        ProjectService projectService = ProjectService.builder()
                .projectsRepository(new StubProjectRepository())
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new Stub1MemberRepository())
                .profileManager(new StubProfileManager())
                .build();

        //when
        String category = "?";

        //then
        Assertions.assertThrows(IllegalArgumentException.class, ()->projectService.makeProjects(null, new ProjectDto("test", "test", category, "", "test", 10000
                , LocalDateTime.now())));
    }

    @Test
    void 프로젝트_불러오기_성공(){
        //given
        ProjectService projectService = ProjectService.builder()
                .projectsRepository(new StubProjectRepository())
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new Stub3MemberRepository())
                .profileManager(new StubProfileManager())
                .build();

        //when
        projectService.makeProjects(null, new ProjectDto("test", "test", "생일", "", "test", 10000
                , LocalDateTime.now()));

        List<ProjectResponse> result = projectService.getProjectList();

        //then
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0).getCurr(), 0);
        Assertions.assertEquals(result.get(0).getCount(), 0);
        Assertions.assertEquals(result.get(0).getTitle(), "test");
        Assertions.assertEquals(result.get(0).getGoal(), 10000);
        Assertions.assertEquals(result.get(0).getWriter(), "tester1");
        Assertions.assertEquals(result.get(0).getIdx(), 0);

    }

    @Test
    void 프로젝트_최신순불러오기_성공(){
        //SQL Problem
    }


    @Test
    void 프로젝트_마감순불러오기_성공(){
        //SQL Problem
    }

    @Test
    void 프로젝트_자세히보기_성공(){
        //given
        ProjectService projectService = ProjectService.builder()
                .projectsRepository(new StubProjectRepository())
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new Stub3MemberRepository())
                .profileManager(new StubProfileManager())
                .build();

        //when
        projectService.makeProjects(null, new ProjectDto("test", "test", "생일", "", "test", 10000
                , LocalDateTime.now()));

        ProjectDetailResponse result = projectService.getProjectDetail(0);

        //then
        Assertions.assertEquals(result.getCategory(), "생일");
        Assertions.assertEquals(result.getCount(), 0);
        Assertions.assertEquals(result.getGoal(), 10000);
        Assertions.assertEquals(result.getWriter(), "tester1");
        Assertions.assertEquals(result.getContent(), "test");
        Assertions.assertEquals(result.getLink(), "test");
        Assertions.assertEquals(result.getCurr(), 0);

    }

    @Test
    void 프로젝트_자세히보기_친구아닐경우(){
        //given
        ProjectService projectService = ProjectService.builder()
                .projectsRepository(new StubProjectRepository())
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new Stub3MemberRepository())
                .profileManager(new StubProfileManager())
                .build();

        //when
        projectService.makeProjects(null, new ProjectDto("test", "test", "생일", "", "test", 10000
                , LocalDateTime.now()));

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(new JwtAuthentication(2, "", "", Role.USER), null, new ArrayList<>()));

        //then

        Assertions.assertThrows(FriendsException.class, ()->projectService.getProjectDetail(0));
    }

    @Test
    void 프로젝트_이미지변경_성공(){
        //given
        ProjectService projectService = ProjectService.builder()
                .projectsRepository(new StubProjectRepository())
                .friendsRepository(new StubFriendRepository())
                .memberRepository(new Stub3MemberRepository())
                .profileManager(new StubProfileManager())
                .build();

        //when
        projectService.makeProjects(null, new ProjectDto("test", "test", "생일", "", "test", 10000
                , LocalDateTime.now()));

        ProjectResponse result = projectService.changeImage(0, new MockMultipartFile("file", new byte[]{}));

        //then
        Assertions.assertEquals(result.getImg(), "newProfile");

    }





}
