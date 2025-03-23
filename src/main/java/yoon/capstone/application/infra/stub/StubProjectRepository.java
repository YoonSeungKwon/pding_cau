package yoon.capstone.application.infra.stub;

import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.service.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

public class StubProjectRepository implements ProjectRepository {
    @Override
    public Optional<Projects> findProject(long index) {
        return Optional.empty();
    }

    @Override
    public Optional<Projects> findProjectFetch(long index) {
        return Optional.empty();
    }

    @Override
    public List<Projects> findAllProjectsLatest(long fromUser) {
        return null;
    }

    @Override
    public List<Projects> findAllProjectsUpcoming(long fromUser) {
        return null;
    }

    @Override
    public Projects save(Projects projects) {
        return null;
    }

    @Override
    public void delete(Projects projects) {

    }
}
