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

    List<Projects> findAllByMembers(Members members);

    Projects findProjectsByProjectIdx(long idx);

    //Projects 의 총금액과 인원수를 업데이트 동시성 이슈
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Projects p JOIN Orders o ON p.projectIdx = o.projects.projectIdx WHERE o.orderIdx = :orderIndex")
    Optional<Projects> findProjectsByOrderIndexWithLock(@Param("orderIndex") long orderIndex);

}
