package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import yoon.capstone.application.common.dto.response.ProjectCache;
import yoon.capstone.application.common.enums.Provider;
import yoon.capstone.application.common.enums.Role;
import yoon.capstone.application.common.util.AesEncryptorManager;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.infrastructure.jpa.*;
import yoon.capstone.application.config.security.JwtProvider;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.manager.MockProfileManager;
import yoon.capstone.application.service.manager.TokenRefreshTemplate;
import yoon.capstone.application.service.repository.MemberRepository;

@SpringBootTest
class ApplicationTests {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	AesBytesEncryptor aesBytesEncryptor;
	@Test
	void contextLoads() {

		MemberService memberService = MemberService.builder()
				.memberRepository(memberRepository)
				.aesEncryptorManager(new AesEncryptorManager(aesBytesEncryptor))
				.profileManager(new MockProfileManager())
				.tokenRefreshTemplate(new TokenRefreshTemplate(new JwtProvider(memberRepository)))
				.build();

		Members members = Members.builder()
				.email("test32123@test.com")
				.password("abcd1234")
				.oauth(false)
				.provider(Provider.DEFAULT)
				.username("tester")
				.role(Role.USER)
				.build();

		JwtAuthentication jwtAuthentication = JwtAuthentication.builder()
				.email(members.getEmail())

				.build();

	}

}
