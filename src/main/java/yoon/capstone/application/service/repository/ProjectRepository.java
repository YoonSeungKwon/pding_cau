package yoon.capstone.application.service.repository;

import yoon.capstone.application.service.domain.Projects;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    Optional<Projects> findProject(long index);

    Optional<Projects> findProjectFetch(long index);

    List<Projects> findAllProjectsLatest(long fromUser);

    List<Projects> findAllProjectsUpcoming(long fromUser);

    Projects save(Projects projects);

    void delete(Projects projects);


}
