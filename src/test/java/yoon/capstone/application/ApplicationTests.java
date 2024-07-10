package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.repository.*;
import yoon.capstone.application.security.JwtProvider;

@SpringBootTest
class ApplicationTests {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	FriendsRepository friendsRepository;

	@Autowired
	ProjectsRepository projectsRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	PaymentRepository paymentRepository;

	@Autowired
	JwtProvider jwtProvider;

	@Test
	@Transactional
	void contextLoads() {



	}

}
