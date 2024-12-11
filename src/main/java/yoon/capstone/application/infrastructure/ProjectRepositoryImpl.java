package yoon.capstone.application.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.infrastructure.jpa.ProjectsJpaRepository;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.service.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectsJpaRepository projectsJpaRepository;

    @Override
    public Optional<Projects> findProject(long index) {
        return projectsJpaRepository.findProjectsByProjectIdx(index);
    }

    @Override
    public Optional<Projects> findProjectFetch(long index) {
        return projectsJpaRepository.findProjectsByProjectIdxWithFetchJoin(index);
    }

    @Override
    public List<Projects> findAllProjectsLatest(long fromUser) {
        return projectsJpaRepository.findProjectsByFriendsFromUserOrderByLatest(fromUser);
    }

    @Override
    public List<Projects> findAllProjectsUpcoming(long fromUser) {
        return projectsJpaRepository.findProjectsByFriendsFromUserOrderByUpcoming(fromUser);
    }

    @Override
    public Projects save(Projects projects) {
        return projectsJpaRepository.save(projects);
    }

    @Override
    public void delete(Projects projects) {
        projectsJpaRepository.delete(projects);
    }
}
