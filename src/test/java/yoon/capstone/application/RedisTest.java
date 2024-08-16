package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.service.TestOrderService;

@SpringBootTest
public class RedisTest {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProjectsRepository projectsRepository;

    @Autowired
    TestOrderService orderService;

    @Test
    void test(){




    }


}
