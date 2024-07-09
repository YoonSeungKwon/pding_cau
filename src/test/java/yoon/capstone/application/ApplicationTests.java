package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.dto.request.MemberSecurityDto;
import yoon.capstone.application.entity.Friends;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.enums.Provider;
import yoon.capstone.application.enums.Role;
import yoon.capstone.application.repository.*;
import yoon.capstone.application.security.JwtProvider;

import java.util.List;

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
