package yoon.capstone.application;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.common.dto.response.ProjectResponse;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.common.enums.Category;
import yoon.capstone.application.common.enums.Provider;
import yoon.capstone.application.common.enums.Role;
import yoon.capstone.application.infrastructure.jpa.MemberJpaRepository;
import yoon.capstone.application.infrastructure.jpa.ProjectsJpaRepository;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.config.security.JwtAuthenticationToken;
import yoon.capstone.application.config.security.JwtProvider;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.ProjectService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@SpringBootTest
public class ProjectQueryTest {

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AesBytesEncryptor aesBytesEncryptor;
    @Autowired
    MemberService memberService;
    @Autowired
    ProjectService projectService;
    @Autowired
    MemberJpaRepository memberRepository;
    @Autowired
    ProjectsJpaRepository projectsRepository;
    @PersistenceContext
    EntityManager entityManager;
    final String phone = "010-1234-5678";

    @Transactional(propagation = Propagation.NESTED)
    void setting (int testSet) {

        List<Members> memberList = new ArrayList<>();


        for (int i = 1; i <= testSet; i++) {

            List<Projects> projectList = new ArrayList<>();

            for(int j=1; j<=testSet; j++){
                Projects projects = Projects.builder()
                        .finishAt(LocalDateTime.now())
                        .goal(10000)
                        .link(null)
                        .option(null)
                        .title("test"+i+j)
                        .content("content"+i+j)
                        .image(null)
                        .category(Category.생일)
                        .build();
                projectList.add(projects);
            }

            projectsRepository.saveAll(projectList);

            Members members = Members.builder().email("test" + i + "@test.com").password("abcd1234")
                    .username("tester" + i).oauth(false).provider(Provider.DEFAULT).role(Role.USER).build();
            byte[] encryptPhone = aesBytesEncryptor.encrypt(phone.getBytes(StandardCharsets.UTF_8));
            String phone = Base64.getEncoder().encodeToString(encryptPhone);
            members.setPhone(phone);
            members.setProjects(projectList);
            memberList.add(members);
        }
        memberRepository.saveAll(memberList);
    }

//    @Test
//    @Transactional
//    void projectsTest(){
//        setting(10);
//
//
//        System.out.println(projectsRepository.findAll().size());
//        entityManager.clear();
//
//        System.out.println("---------------------------------------");
//
//        List<Members> membersList = memberRepository.findAllWithJoinFetch();
//
//        List<String> list = new ArrayList<>();
//
//        for(Members member : membersList){
//            for(Projects project : member.getProjects()){
//                list.add(project.getTitle());
//            }
//        }
//
//
//        Assertions.assertTrue(list.isEmpty());
//
//        System.out.println("---------------------------------------");
//    }

    @Test
    @Transactional
    void getAllProjects(){

        setting(20);

        entityManager.clear();

        Members members = memberRepository.findMembersByEmail("test1@test.com").orElseThrow(()->new UsernameNotFoundException(null));

        JwtAuthentication jwtAuthentication = new JwtAuthentication(members.getMemberIdx(), members.getEmail(), members.getRefreshToken(), members.getRole());

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwtAuthentication, null, members.getAuthority());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("-------------------------------------------------------------");
        List<ProjectResponse> projects = projectService.getProjectList();
        System.out.println("-------------------------------------------------------------");
    }

}
