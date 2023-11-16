package yoon.capstone.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectsRepository projectsRepository;
    private final MemberRepository memberRepository;
    private final FriendsRepository friendsRepository;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "cau-artech-capstone";

    private ProjectResponse toResponse(Projects projects){
        return new ProjectResponse(projects.getIdx(), projects.getTitle(), projects.getImg(), projects.getGoal(),
                projects.getCurr(), projects.getEnddate());
    }

    private ProjectDetailResponse toDetailResponse(Projects projects){
        return new ProjectDetailResponse(projects.getTitle(), projects.getContent(), projects.getOption(),
                projects.getImg(), projects.getLink(), projects.getGoal(), projects.getCurr(), projects.getEnddate());
    }

    public ProjectResponse makeProjects(String email, boolean oauth, MultipartFile file, ProjectDto dto) {
        Members me = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Members members = memberRepository.findMembersByEmailAndOauth(email, oauth);
        String url;
        if(!me.equals(members))
            throw new ProjectException(ErrorCode.PROJECT_OWNER.getStatus());
        UUID uuid = UUID.randomUUID();
        try {
            String fileName = uuid + file.getOriginalFilename();
            String fileUrl = "https://" + bucket + "/projects/" + fileName;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            System.out.println(file.getContentType());
            url = fileUrl;
            amazonS3Client.putObject(bucket +"/projects", fileName, file.getInputStream(), objectMetadata);
        } catch (Exception e){
            throw new ProjectException(null);
        }
        Projects projects = Projects.builder()
                .members(members)
                .title(dto.getTitle())
                .content(dto.getContent())
                .link(dto.getLink())
                .option(dto.getOption())
                .img(url)
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
