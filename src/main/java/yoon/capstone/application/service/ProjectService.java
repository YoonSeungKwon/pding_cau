package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.domain.Projects;
import yoon.capstone.application.enums.ErrorCode;
import yoon.capstone.application.exception.ProjectException;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.vo.request.ProjectDto;
import yoon.capstone.application.vo.response.ProjectDetailResponse;
import yoon.capstone.application.vo.response.ProjectResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectsRepository projectsRepository;
    private final MemberRepository memberRepository;

    private ProjectResponse toResponse(Projects projects){
        return new ProjectResponse(projects.getIdx(), projects.getTitle(), projects.getImg(), projects.getGoal(),
                projects.getCurr(), projects.getEnddate());
    }

    private ProjectDetailResponse toDetailResponse(Projects projects){
        return new ProjectDetailResponse(projects.getTitle(), projects.getContent(), projects.getImg(),
                projects.getLink(), projects.getGoal(), projects.getCurr(), projects.getEnddate());
    }

    public ProjectResponse makeProjects(ProjectDto dto){
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Projects projects = Projects.builder()
                .members(members)
                .title(dto.getTitle())
                .content(dto.getContent())
                .link(dto.getLink())
                .img(dto.getImg())
                .goal(dto.getGoal())
                .enddate(dto.getEnddate())
                .category(dto.getCategory())
                .build();
        return toResponse(projectsRepository.save(projects));
    }
    public List<ProjectResponse> getProjectList(String email){
        Members members = memberRepository.findMembersByEmail(email);
        if(members == null)
            throw new UsernameNotFoundException(email);

        List<Projects> list = projectsRepository.findAllByMembers(members);
        List<ProjectResponse> result = new ArrayList<>();

        for(Projects p:list){
            result.add(toResponse(p));
        }

        return result;
    }

    public ProjectDetailResponse getProjectDetail(long idx){
        Projects projects = projectsRepository.findProjectsByIdx(idx);
        return toDetailResponse(projects);
    }

    public ProjectResponse deleteProjects(long idx){
        Projects projects = projectsRepository.findProjectsByIdx(idx);
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!projects.getMembers().equals(members))
            throw new ProjectException(ErrorCode.PROJECT_OWNER.getStatus());

        projectsRepository.delete(projects);

        return toResponse(projects);
    }

}
