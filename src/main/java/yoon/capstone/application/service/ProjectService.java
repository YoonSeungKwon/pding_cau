package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.common.annotation.Authenticated;
import yoon.capstone.application.common.dto.request.ProjectDto;
import yoon.capstone.application.common.dto.response.ProjectDetailResponse;
import yoon.capstone.application.common.dto.response.ProjectResponse;
import yoon.capstone.application.service.domain.Friends;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.common.enums.Category;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.exception.FriendsException;
import yoon.capstone.application.common.exception.ProjectException;
import yoon.capstone.application.common.exception.UnauthorizedException;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.service.manager.ProfileManager;
import yoon.capstone.application.service.repository.FriendRepository;
import yoon.capstone.application.service.repository.MemberRepository;
import yoon.capstone.application.service.repository.ProjectRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectsRepository;

    private final MemberRepository memberRepository;

    private final FriendRepository friendsRepository;

    private final ProfileManager profileManager;

    private ProjectResponse toResponse(Projects projects){
        return new ProjectResponse(projects.getProjectIdx(), projects.getMembers().getUsername(), projects.getTitle(), projects.getImage(), projects.getGoalAmount(),
                projects.getCurrentAmount(), projects.getParticipantsCount(), projects.getCategory().getValue(),
                projects.getCreatedAt().toString(), projects.getFinishAt().toString(), projects.getMembers().getProfile());
    }

    private ProjectDetailResponse toDetailResponse(Projects projects){
        return new ProjectDetailResponse(projects.getTitle(), projects.getContent(), projects.getMembers().getUsername(), projects.getMembers().getProfile(),
                projects.getOption(), projects.getCategory().getValue(), projects.getImage(), projects.getLink(), projects.getGoalAmount(), projects.getCurrentAmount()
                ,projects.getParticipantsCount(), projects.getCreatedAt().toString(), projects.getFinishAt().toString());
    }

    @Transactional
    @Authenticated
    public List<ProjectResponse> makeProjects(MultipartFile file, ProjectDto dto) {
        JwtAuthentication memberDto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Eagle Loading
        Members currentMember = memberRepository.findMemberFetch(memberDto.getMemberIdx())
                .orElseThrow(()->new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

        String url = profileManager.updateProject(file, Category.valueOf(dto.getCategory()));

        Projects projects = Projects.builder()
                .members(currentMember)
                .title(dto.getTitle())
                .content(dto.getContent())
                .link(dto.getLink())
                .option(dto.getOption())
                .image(url)
                .goal(dto.getGoal())
                .finishAt(dto.getEnddate())
                .category(Category.valueOf(dto.getCategory()))
                .build();

        currentMember.getProjects().add(projects);
        memberRepository.save(currentMember);

        List<ProjectResponse> result = new ArrayList<>();
        for(Projects p: currentMember.getProjects()){
            result.add(toResponse(p));
        }
        return result;
    }
    @Authenticated
    public List<ProjectResponse> getProjectList(){
        JwtAuthentication memberDto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Eagle Loading
        Members currentMember = memberRepository.findMemberFetch(memberDto.getMemberIdx())
                .orElseThrow(()->new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

        List<ProjectResponse> result = new ArrayList<>();
        List<Projects> list = currentMember.getProjects();

        for(Projects p:list){
            result.add(toResponse(p));
        }

        return result;
    }

    @Authenticated
    public List<ProjectResponse> getFriendsListLatest(){
        JwtAuthentication memberDto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Eagle Loading
        List<Projects> list = projectsRepository.findAllProjectsLatest(memberDto.getMemberIdx());

        return list.stream().map((this::toResponse)).toList();
    }

    @Authenticated
    public List<ProjectResponse> getFriendsListUpcoming(){
        JwtAuthentication memberDto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Eagle Loading
        List<Projects> list = projectsRepository.findAllProjectsUpcoming(memberDto.getMemberIdx());

        return list.stream().map((this::toResponse)).toList();
    }

    @Authenticated
    public ProjectDetailResponse getProjectDetail(long projectsIdx){
        JwtAuthentication memberDto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Eagle Loading
        Projects projects = projectsRepository.findProjectFetch(projectsIdx).orElseThrow(()->new ProjectException(ExceptionCode.PROJECT_NOT_FOUND));
        Members members = projects.getMembers();

        Friends friends = friendsRepository.findFriend(members, memberDto.getMemberIdx(), true).orElseThrow(
                ()->new FriendsException(ExceptionCode.NOT_FRIENDS));

        if(members.getMemberIdx() != memberDto.getMemberIdx() && !friends.isFriends())
            throw new FriendsException(ExceptionCode.NOT_FRIENDS);

        return toDetailResponse(projects);
    }

    @Transactional
    @Authenticated
    public void deleteProjects(long idx){
        //Eagle Loading
        Projects projects = projectsRepository.findProjectFetch(idx).orElseThrow(()->new ProjectException(ExceptionCode.PROJECT_NOT_FOUND));
        JwtAuthentication memberDto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(projects.getMembers().getMemberIdx() != memberDto.getMemberIdx())
            throw new ProjectException(ExceptionCode.PROJECT_OWNER);

        projectsRepository.delete(projects);

    }

    @Transactional
    public ProjectResponse changeImage(long idx, MultipartFile file){
        //Lazy Loading
        Projects projects = projectsRepository.findProject(idx).orElseThrow(()->new ProjectException(ExceptionCode.PROJECT_NOT_FOUND));

        String url = profileManager.updateProject(file, projects.getCategory());
        String prevImg = projects.getImage();

        profileManager.deleteImage(prevImg);

        projects.setImage(url);

        return toResponse(projectsRepository.save(projects));
    }

}
