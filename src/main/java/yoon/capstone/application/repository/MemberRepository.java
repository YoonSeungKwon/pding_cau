package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.entity.Members;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Members, Long> {

    Members findMembersByMemberIdx(long idx);
    Members findMembersByEmail(String email);
    Members findMembersByRefreshToken(String token);
    Members findMembersByEmailAndOauth(String email, boolean oauth);
    List<Members> findAllByEmail(String email);
    boolean existsByEmail(String email);


}
