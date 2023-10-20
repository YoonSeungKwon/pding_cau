package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.domain.Projects;
import yoon.capstone.application.repository.CartRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.vo.request.ProjectDto;
import yoon.capstone.application.vo.response.ProjectResponse;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final CartRepository cartRepository;
    private final ProjectsRepository projectsRepository;

    private ProjectResponse toResponse(Projects projects){
        return new ProjectResponse(projects.getCarts().getMembers().getUsername(), projects.getName(),
                projects.getInfo(), projects.getGoal(), projects.getCurr(), projects.getEnddate());
    }

    public ProjectResponse makeProjects(ProjectDto dto){
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Projects projects = Projects.builder()
                .carts(cartRepository.getCartsByMembers(members))
                .name(dto.getName())
                .info(dto.getInfo())
                .goal(dto.getGoal())
                .enddate(dto.getEnddate())
                .build();

        return toResponse(projectsRepository.save(projects));
    }


}
