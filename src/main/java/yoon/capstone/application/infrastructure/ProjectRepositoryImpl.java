package yoon.capstone.application.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.infrastructure.jpa.ProjectsJpaRepository;
import yoon.capstone.application.service.repository.ProjectRepository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectsJpaRepository projectsJpaRepository;

}
