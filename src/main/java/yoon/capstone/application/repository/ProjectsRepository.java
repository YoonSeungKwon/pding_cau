package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.domain.Projects;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {

    List<Projects> findAllByMembers(Members members);

    Projects findProjectsByIdx(long idx);
}
