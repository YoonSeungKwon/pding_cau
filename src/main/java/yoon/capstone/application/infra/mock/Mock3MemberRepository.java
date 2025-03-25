package yoon.capstone.application.infra.mock;

import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.service.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Mock3MemberRepository implements MemberRepository {

    private List<Members> list = new ArrayList<>();

    @Override
    public Optional<Members> findMember(long index) {
        for(Members members : list){
            if(members.getMemberIdx() == index)return Optional.of(members);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Members> findMemberFetch(long index) {

        for(Members m : list){
            if(m.getMemberIdx() == 1) return Optional.of(m);
        }

        if(index != 1)return Optional.empty();
        return Optional.of(Members.builder().index(index).email("test"+index+"@test.com").username("tester"+index).build());
    }

    @Override
    public Optional<Members> findMember(String email) {
        for(Members members : list){
            if(members.getEmail().equals(email))return Optional.of(members);
        }
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
        for(Members members : list){
            if(members.getEmail().equals(email))return true;
        }
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
        list.add(members);

        for(Projects p:members.getProjects()){
            p.setCreatedAt(LocalDateTime.now());
        }
        return members;
    }

}
