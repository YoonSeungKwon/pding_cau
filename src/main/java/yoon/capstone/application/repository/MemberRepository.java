package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.security.JwtAuthentication;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Members, Long> {

    //Lazy Loading
    Optional<Members> findMembersByMemberIdx(long idx);

    //Eagle Loading
    @Query("SELECT DISTINCT m FROM Members m JOIN FETCH m.projects WHERE m.memberIdx = :memberIndex")
    Optional<Members> findMembersByMemberIdxWithFetchJoin(@Param("memberIndex") long idx);

    //Lazy Loading
    Optional<Members> findMembersByEmail(String email);

    //Eagle Loading Members By FromUser
    @Query("SELECT m FROM Members m INNER JOIN FETCH m.projects INNER JOIN Friends f ON m.memberIdx = f.fromUser WHERE f.fromUser = :fromUser")
    List<Members> findAllByFromUserWithFetchJoin(@Param("fromUser") long fromUser);

    //Boolean
    boolean existsByEmail(String email);

    //Security DTO Authentication
    @Query("SELECT new yoon.capstone.application.security.JwtAuthentication(m.memberIdx, m.email, m.refreshToken, m.role) " +
            "FROM Members m WHERE m.email = :email")
    JwtAuthentication findMemberDtoWithEmail(@Param("email") String email);

    //Security DTO Refresh Token
    @Query("SELECT new yoon.capstone.application.security.JwtAuthentication(m.memberIdx, m.email, m.refreshToken, m.role) " +
            "FROM Members m WHERE m.refreshToken = :token")
    JwtAuthentication findMemberDtoWithToken(@Param("token") String token);


}
