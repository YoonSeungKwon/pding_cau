package yoon.capstone.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
import yoon.capstone.application.common.exception.UtilException;
import yoon.capstone.application.infrastructure.jpa.FriendsJpaRepository;
import yoon.capstone.application.infrastructure.jpa.MemberJpaRepository;
import yoon.capstone.application.infrastructure.jpa.ProjectsJpaRepository;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.service.manager.ProfileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectsJpaRepository projectsRepository;

    private final MemberJpaRepository memberRepository;

    private final FriendsJpaRepository friendsRepository;

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
    public List<ProjectResponse> makeProjects(MultipartFile file, ProjectDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Eagle Loading

        Members currentMember = memberRepository.findMembersByMemberIdxWithFetchJoin(memberDto.getMemberIdx())
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
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectList(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Eagle Loading
        Members currentMember = memberRepository.findMembersByMemberIdxWithFetchJoin(memberDto.getMemberIdx())
                .orElseThrow(()->new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

        List<ProjectResponse> result = new ArrayList<>();
        List<Projects> list = currentMember.getProjects();

        for(Projects p:list){
            result.add(toResponse(p));
        }

        return result;
    }

    public List<ProjectResponse> getFriendsListLatest(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Eagle Loading
        List<Projects> list = projectsRepository.findProjectsByFriendsFromUserOrderByLatest(memberDto.getMemberIdx());

        return list.stream().map((this::toResponse)).toList();
    }

    public List<ProjectResponse> getFriendsListUpcoming(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Eagle Loading
        List<Projects> list = projectsRepository.findProjectsByFriendsFromUserOrderByUpcoming(memberDto.getMemberIdx());

        return list.stream().map((this::toResponse)).toList();
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetail(long projectsIdx){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Eagle Loading
        Projects projects = projectsRepository.findProjectsByProjectIdxWithFetchJoin(projectsIdx);
        Members members = projects.getMembers();

        Friends friends = friendsRepository.findFriendsByToUserAndFromUserAndFriends(members, memberDto.getMemberIdx(), true).orElseThrow(
                ()->new FriendsException(ExceptionCode.NOT_FRIENDS));

        if(members.getMemberIdx() != memberDto.getMemberIdx() && !friends.isFriends())
            throw new FriendsException(ExceptionCode.NOT_FRIENDS);

        return toDetailResponse(projects);
    }

    @Transactional
    public void deleteProjects(long idx){
        //Eagle Loading
        Projects projects = projectsRepository.findProjectsByProjectIdxWithFetchJoin(idx);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        if(projects.getMembers().getMemberIdx() != memberDto.getMemberIdx())
            throw new ProjectException(ExceptionCode.PROJECT_OWNER);

        projectsRepository.delete(projects);

    }

    @Transactional
    public ProjectResponse changeImage(long idx, MultipartFile file){
        //Lazy Loading
        Projects projects = projectsRepository.findProjectsByProjectIdx(idx);

        String url = profileManager.updateProject(file, projects.getCategory());
        String prevImg = projects.getImage();

        profileManager.deleteImage(prevImg);

        projects.setImage(url);

        return toResponse(projectsRepository.save(projects));
    }

}
