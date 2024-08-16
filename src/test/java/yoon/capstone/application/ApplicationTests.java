package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.dto.request.RegisterDto;
import yoon.capstone.application.dto.response.ProjectCache;
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

	@Autowired
	RedissonClient redissonClient;

	@Test
	void contextLoads() {

		RBucket<ProjectCache> rBucket = redissonClient.getBucket("projects::"+506);

		ProjectCache cache = rBucket.get();

		System.out.println("Current: " + cache.getCurrentAmount());
		System.out.println("Participant: " + cache.getParticipantsCount());

	}

}
