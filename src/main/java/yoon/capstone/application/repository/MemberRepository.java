package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.entity.Members;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Members, Long> {

    Members findMembersByMemberIdx(long idx);
    Members findMembersByEmail(String email);
    Members findMembersByRefreshToken(String token);
    @Query("SELECT m FROM Members m WHERE m.email = :email AND m.isOauth = :oauth")
    Members findMembersWithOauth(@Param("email") String email, @Param("oauth") boolean isOauth);
    List<Members> findAllByEmail(String email);
    boolean existsByEmail(String email);


}
