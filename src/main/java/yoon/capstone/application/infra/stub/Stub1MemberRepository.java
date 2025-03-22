package yoon.capstone.application.infra.stub;

import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.repository.MemberRepository;

import java.util.List;
import java.util.Optional;


public class Stub1MemberRepository implements MemberRepository {

    Members testMember(int index){
        return Members.builder().index(index).email("test"+index+"@test.com").username("tester"+index).build();
    }
    @Override
    public Optional<Members> findMember(long index) {
        return Optional.of(testMember(2));
    }

    @Override
    public Optional<Members> findMemberFetch(long index) {
        return Optional.empty();
    }

    @Override
    public Optional<Members> findMember(String email) {
        return Optional.empty();
    }

    @Override
    public List<Members> findMemberFetch(String email) {
        return null;
    }

    @Override
    public Optional<Members> findFriendMember(long index, long fromUser) {
        return Optional.empty();
    }

    @Override
    public boolean checkEmail(String email) {
        return false;
    }

    @Override
    public JwtAuthentication findAuthenticationWithEmail(String email) {
        return null;
    }

    @Override
    public JwtAuthentication findAuthenticationWithToken(String token) {
        return null;
    }

    @Override
    public Members save(Members members) {
        return null;
    }
}
