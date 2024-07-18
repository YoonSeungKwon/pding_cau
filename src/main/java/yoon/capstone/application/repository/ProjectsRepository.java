package yoon.capstone.application.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.entity.Projects;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {

    //Lazy Loading
    Projects findProjectsByProjectIdx(long idx);

    //Eagle Loading
    @Query("SELECT p FROM Projects p JOIN FETCH p.members WHERE p.projectIdx = :projectIndex")
    Projects findProjectsByProjectIdxWithFetchJoin(@Param("projectIndex") long projectIndex);

    //Cost, Total Pessimistic Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Projects p JOIN Orders o ON p.projectIdx = o.projects.projectIdx WHERE o.orderIdx = :orderIndex")
    Optional<Projects> findProjectsByOrderIndexWithLock(@Param("orderIndex") long orderIndex);

}
