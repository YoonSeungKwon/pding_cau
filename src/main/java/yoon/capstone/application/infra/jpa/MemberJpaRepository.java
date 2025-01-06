package yoon.capstone.application.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.config.security.JwtAuthentication;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<Members, Long> {

    //Lazy Loading
    Optional<Members> findMembersByMemberIdx(long idx);

    //Eagle Loading
    @Query("SELECT DISTINCT m FROM Members m LEFT JOIN FETCH m.projects WHERE m.memberIdx = :memberIndex")
    Optional<Members> findMembersByMemberIdxWithFetchJoin(@Param("memberIndex") long idx);

    //Lazy Loading
    Optional<Members> findMembersByEmail(String email);

    @Query("SELECT DISTINCT m FROM Members m WHERE m.email LIKE :email%")
    List<Members> findMembersByEmailLikeString(@Param("email") String email);

    //Eagle Loading Members By FromUser
    @Query("SELECT DISTINCT m FROM Members m LEFT JOIN FETCH m.projects INNER JOIN Friends f ON m.memberIdx = f.toUser.memberIdx WHERE f.fromUser = :fromUser")
    List<Members> findAllByFromUserWithFetchJoin(@Param("fromUser") long fromUser);

    @Query("SELECT m FROM Members m JOIN Friends f ON m.memberIdx = f.toUser.memberIdx WHERE f.isFriends = true AND m.memberIdx = :memberIdx AND f.fromUser = :fromUser")
    Optional<Members> findMembersByMemberIdxAndIsFriend(@Param("memberIdx") long idx, @Param("fromUser") long fromUser);

    //Boolean
    boolean existsByEmail(String email);

    //Security DTO Authentication
    @Query("SELECT new yoon.capstone.application.config.security.JwtAuthentication(m.memberIdx, m.email, m.refreshToken, m.role) " +
            "FROM Members m WHERE m.email = :email")
    JwtAuthentication findMemberDtoWithEmail(@Param("email") String email);

    //Security DTO Refresh Token
    @Query("SELECT new yoon.capstone.application.config.security.JwtAuthentication(m.memberIdx, m.email, m.refreshToken, m.role) " +
            "FROM Members m WHERE m.refreshToken = :token")
    JwtAuthentication findMemberDtoWithToken(@Param("token") String token);


}
