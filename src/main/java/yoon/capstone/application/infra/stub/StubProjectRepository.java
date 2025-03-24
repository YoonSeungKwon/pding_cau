package yoon.capstone.application.infra.stub;

import org.springframework.security.core.parameters.P;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.service.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StubProjectRepository implements ProjectRepository {

    List<Projects> list = new ArrayList<>();

    @Override
    public Optional<Projects> findProject(long index) {
        for(Projects p : list){
            if(p.getProjectIdx() == index)return Optional.of(p);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Projects> findProjectFetch(long index) {
        for(Projects p: list){
            if(p.getProjectIdx() == index)return Optional.of(p);
        }
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
        list.add(projects);
        return projects;
    }

    @Override
    public void delete(Projects projects) {

    }
}
