package yoon.capstone.application.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.service.domain.Projects;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectsJpaRepository extends JpaRepository<Projects, Long> {

    //Lazy Loading
    Optional<Projects> findProjectsByProjectIdx(long idx);

    //Eagle Loading
    @Query("SELECT p FROM Projects p JOIN FETCH p.members WHERE p.projectIdx = :projectIndex")
    Optional<Projects> findProjectsByProjectIdxWithFetchJoin(@Param("projectIndex") long projectIndex);

    @Query("SELECT p FROM Projects p JOIN FETCH p.members INNER JOIN Friends f ON p.members.memberIdx = f.toUser.memberIdx " +
            "WHERE f.fromUser = :fromUser AND p.finishAt > CURRENT_TIMESTAMP ORDER BY p.createdAt DESC")
    List<Projects> findProjectsByFriendsFromUserOrderByLatest(@Param("fromUser") long fromUser);

    @Query("SELECT p FROM Projects p JOIN FETCH p.members INNER JOIN Friends f ON p.members.memberIdx = f.toUser.memberIdx " +
            "WHERE f.fromUser = :fromUser AND p.finishAt > CURRENT_TIMESTAMP ORDER BY p.finishAt")
    List<Projects> findProjectsByFriendsFromUserOrderByUpcoming(@Param("fromUser") long fromUser);


}
