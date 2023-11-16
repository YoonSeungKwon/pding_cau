package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import yoon.capstone.application.domain.Friends;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.domain.Projects;
import yoon.capstone.application.enums.ErrorCode;
import yoon.capstone.application.exception.FriendsException;
import yoon.capstone.application.exception.ProjectException;
import yoon.capstone.application.repository.FriendsRepository;
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
    private final FriendsRepository friendsRepository;

    private ProjectResponse toResponse(Projects projects){
        return new ProjectResponse(projects.getIdx(), projects.getTitle(), projects.getImg(), projects.getGoal(),
                projects.getCurr(), projects.getEnddate());
    }

    private ProjectDetailResponse toDetailResponse(Projects projects){
        return new ProjectDetailResponse(projects.getTitle(), projects.getContent(), projects.getOption(),
                projects.getImg(), projects.getLink(), projects.getGoal(), projects.getCurr(), projects.getEnddate());
    }

    public ProjectResponse makeProjects(String email, boolean oauth, ProjectDto dto){
        Members me = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Members members = memberRepository.findMembersByEmailAndOauth(email, oauth);

        if(!me.equals(members))
            throw new ProjectException(ErrorCode.PROJECT_OWNER.getStatus());


        Projects projects = Projects.builder()
                .members(members)
                .title(dto.getTitle())
                .content(dto.getContent())
                .link(dto.getLink())
                .option(dto.getOption())
                .img(dto.getImg())
                .goal(dto.getGoal())
                .enddate(dto.getEnddate())
                .category(dto.getCategory())
                .build();
        return toResponse(projectsRepository.save(projects));
    }
    public List<ProjectResponse> getProjectList(String email, boolean oauth){
        Members members = memberRepository.findMembersByEmailAndOauth(email, oauth);
        Members me = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(members == null)
            throw new UsernameNotFoundException(email);

        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(members, me.getIdx());

        if(!members.equals(me) && (friends == null || !friends.isFriends()))
            throw new FriendsException(ErrorCode.NOT_FRIENDS.getStatus());

        List<Projects> list = projectsRepository.findAllByMembers(members);
        List<ProjectResponse> result = new ArrayList<>();

        for(Projects p:list){
            result.add(toResponse(p));
        }

        return result;
    }

    public ProjectDetailResponse getProjectDetail(String email, boolean oauth, long idx){
        Members me = (Members)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Members members = memberRepository.findMembersByEmailAndOauth(email, oauth);
        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(members, me.getIdx());

        if(!members.equals(me) &&(friends == null || !friends.isFriends()))
            throw new FriendsException(ErrorCode.NOT_FRIENDS.getStatus());

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
