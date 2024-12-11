package yoon.capstone.application.service.repository;

import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.service.domain.Members;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Optional<Members> findMember(long index);

    Optional<Members> findMemberFetch(long index);

    Optional<Members> findMember(String email);

    List<Members> findMemberFetch(String email);

    Optional<Members> findFriendMember(long index, long fromUser);

    boolean checkEmail(String email);

    JwtAuthentication findAuthenticationWithEmail(String email);

    JwtAuthentication findAuthenticationWithToken(String token);

    Members save(Members members);

}
