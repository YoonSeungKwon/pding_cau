package yoon.capstone.application;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.entity.Projects;
import yoon.capstone.application.enums.Category;
import yoon.capstone.application.enums.Provider;
import yoon.capstone.application.enums.Role;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.security.JwtProvider;
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
    MemberRepository memberRepository;

    @Autowired
    ProjectsRepository projectsRepository;

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
    void memberTest(){

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
