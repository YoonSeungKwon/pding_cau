package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.domain.Members;

@Repository
public interface MemberRepository extends JpaRepository<Members, Long> {

    Members findMembersByIdx(long idx);
    Members findMembersByEmail(String email);
    Members findMembersByRefreshToken(String token);
    boolean existsByEmail(String email);


}
