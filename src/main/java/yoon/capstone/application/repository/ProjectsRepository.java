package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.domain.Projects;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {


}
