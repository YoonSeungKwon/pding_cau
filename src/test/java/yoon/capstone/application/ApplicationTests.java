package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yoon.capstone.application.common.dto.response.ProjectCache;
import yoon.capstone.application.infrastructure.jpa.*;
import yoon.capstone.application.config.security.JwtProvider;
import yoon.capstone.application.service.MemberService;

@SpringBootTest
class ApplicationTests {

	@Autowired
	MemberJpaRepository memberRepository;

	@Autowired
	MemberService memberService;

	@Autowired
	FriendsJpaRepository friendsRepository;

	@Autowired
	ProjectsJpaRepository projectsRepository;

	@Autowired
	OrderJpaRepository orderRepository;

	@Autowired
	PaymentJpaRepository paymentRepository;

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
