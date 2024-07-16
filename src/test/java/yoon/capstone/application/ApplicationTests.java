package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.dto.request.RegisterDto;
import yoon.capstone.application.repository.*;
import yoon.capstone.application.security.JwtProvider;
import yoon.capstone.application.service.MemberService;

@SpringBootTest
class ApplicationTests {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	MemberService memberService;

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
	void contextLoads() {


	}

}
