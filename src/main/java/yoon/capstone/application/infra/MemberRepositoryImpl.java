package yoon.capstone.application.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.infra.jpa.MemberJpaRepository;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;


    @Override
    public Optional<Members> findMember(long index) {
        return memberJpaRepository.findMembersByMemberIdx(index);
    }

    @Override
    public Optional<Members> findMemberFetch(long index) {
        return memberJpaRepository.findMembersByMemberIdxWithFetchJoin(index);
    }

    @Override
    public Optional<Members> findMember(String email) {
        return memberJpaRepository.findMembersByEmail(email);
    }

    @Override
    public List<Members> findMemberFetch(String email) {
        return memberJpaRepository.findMembersByEmailLikeString(email);
    }

    @Override
    public Optional<Members> findFriendMember(long index, long fromUser) {
        return memberJpaRepository.findMembersByMemberIdxAndIsFriend(index, fromUser);
    }

    @Override
    public boolean checkEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public JwtAuthentication findAuthenticationWithEmail(String email) {
        return memberJpaRepository.findMemberDtoWithEmail(email);
    }

    @Override
    public JwtAuthentication findAuthenticationWithToken(String token) {
        return memberJpaRepository.findMemberDtoWithToken(token);
    }

    @Override
    public Members save(Members members) {
        return memberJpaRepository.save(members);
    }
}
