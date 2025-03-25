package yoon.capstone.application;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.common.enums.Category;
import yoon.capstone.application.common.enums.Provider;
import yoon.capstone.application.common.enums.Role;
import yoon.capstone.application.infra.jpa.MemberJpaRepository;
import yoon.capstone.application.infra.jpa.ProjectsJpaRepository;
import yoon.capstone.application.config.security.JwtProvider;
import yoon.capstone.application.service.MemberService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@SpringBootTest
public class MemberQueryTest {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AesBytesEncryptor aesBytesEncryptor;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberJpaRepository memberRepository;

    @Autowired
    ProjectsJpaRepository projectsRepository;

    @PersistenceContext
    EntityManager entityManager;

    private final String phone = "010-1234-5678";

    @Transactional(propagation = Propagation.NESTED)
    void setting(int testSet) {


        List<Members> memberList = new ArrayList<>();
        List<Projects> projectList = new ArrayList<>();


        for (int i = 0; i < testSet; i++) {
            StringBuilder sb = new StringBuilder();
            Members members = Members.builder().email(sb.append("test").append(i).append("@test.com").append("?").append(Provider.DEFAULT.getProvider()).toString()).password(passwordEncoder.encode("abcd1234"))
                    .username("tester" + i).oauth(false).provider(Provider.DEFAULT).role(Role.USER).build();
            byte[] encryptPhone = aesBytesEncryptor.encrypt(phone.getBytes(StandardCharsets.UTF_8));
            String phone = Base64.getEncoder().encodeToString(encryptPhone);
            members.setPhone(phone);

            for(int j=0; j<testSet; j++){
                Projects projects = Projects.builder()
                        .members(members)
                        .finishAt(LocalDateTime.now())
                        .goal(10000)
                        .link(null)
                        .option(null)
                        .title("test"+j)
                        .content("content"+j)
                        .image(null)
                        .category(Category.생일)
                        .build();
                projectList.add(projects);
            }
            members.setProjects(projectList);
            memberList.add(members);
        }





        memberRepository.saveAll(memberList);
    }

    @Test
//    @Transactional
    void 멤버_쿼리수_테스트(){

        setting(10);

        System.out.println("---------------Find By Email------------------");

//        memberService.findMember("test1@test.com");
//        entityManager.clear();
        System.out.println("---------------Find By Email------------------");


//        Members testUser = memberRepository.findMembersByEmail("test3@test.com").orElseThrow(()->new UsernameNotFoundException(null));
//        entityManager.persist(testUser);

        System.out.println("---------------Change Profile------------------");

//        testUser.setProfile(" hi ");
//        memberRepository.save(testUser);
//
//        entityManager.flush();
//        entityManager.clear();
        System.out.println("---------------Change Profile------------------");




    }

}
