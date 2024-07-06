package yoon.capstone.application.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.entity.Projects;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {

    List<Projects> findAllByMembers(Members members);

    Projects findProjectsByIdx(long idx);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Projects findProjectsByTitle(String title);

}
