package yoon.capstone.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.entity.Friends;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.entity.Projects;
import yoon.capstone.application.enums.Categorys;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.exception.FriendsException;
import yoon.capstone.application.exception.ProjectException;
import yoon.capstone.application.exception.UnauthorizedException;
import yoon.capstone.application.exception.UtilException;
import yoon.capstone.application.repository.FriendsRepository;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.dto.request.ProjectDto;
import yoon.capstone.application.dto.response.ProjectDetailResponse;
import yoon.capstone.application.dto.response.ProjectResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectsRepository projectsRepository;
    private final MemberRepository memberRepository;
    private final FriendsRepository friendsRepository;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "pding-storage";
    private final String region = "ap-northeast-2";


    private ProjectResponse toResponse(Projects projects){
        return new ProjectResponse(projects.getProjectIdx(), projects.getMembers().getUsername(), projects.getTitle(), projects.getImg(), projects.getGoalAmount(),
                projects.getCurrentAmount(), projects.getCount(), projects.getCategory().getValue(), projects.getFinishAt(), projects.getMembers().getProfile());
    }

    private ProjectDetailResponse toDetailResponse(Projects projects){
        return new ProjectDetailResponse(projects.getTitle(), projects.getContent(), projects.getMembers().getUsername(), projects.getMembers().getProfile(),
                projects.getOption(), projects.getCategory().getValue(), projects.getImg(), projects.getLink(), projects.getGoalAmount(), projects.getCurrentAmount()
                ,projects.getCount(), projects.getCreatedAt(), projects.getFinishAt());
    }

    @CachePut(value = "myProjectList", key = "#email")
    @Transactional
    public List<ProjectResponse> makeProjects(MultipartFile file, ProjectDto dto, String email) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        String url;
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
            throw new UtilException(ExceptionCode.NOT_IMAGE_FORMAT);
        }
        UUID uuid = UUID.randomUUID();
        try {
            String fileName = uuid + file.getOriginalFilename();
            String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/projects/" + fileName;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            System.out.println(file.getContentType());
            url = fileUrl;
            amazonS3Client.putObject(bucket +"/projects", fileName, file.getInputStream(), objectMetadata);
        } catch (Exception e){
            throw new ProjectException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
        Categorys categorys;
        if(dto.getCategory().equals(Categorys.생일.getValue())){
            categorys = Categorys.생일;
        }else{
            categorys = Categorys.졸업;
        }

        Projects projects = Projects.builder()
                .members(currentMember)
                .title(dto.getTitle())
                .content(dto.getContent())
                .link(dto.getLink())
                .option(dto.getOption())
                .img(url)
                .goal(dto.getGoal())
                .finishAt(dto.getEnddate())
                .category(categorys)
                .build();
        projectsRepository.save(projects);

        List<ProjectResponse> result = new ArrayList<>();
        List<Projects> list = projectsRepository.findAllByMembers(currentMember);
        for(Projects p: list){
            result.add(toResponse(p));
        }
        return result;
    }
    @Cacheable(value = "myProjectList", key = "#email")
    public List<ProjectResponse> getProjectList(String email){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        List<ProjectResponse> result = new ArrayList<>();
        List<Projects> list = projectsRepository.findAllByMembers(currentMember);

        for(Projects p:list){
            result.add(toResponse(p));
        }

        return result;
    }

    @Cacheable(value = "projectList", key = "#email")
    public List<ProjectResponse> getFriendsList(String email){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        List<ProjectResponse> result = new ArrayList<>();
        List<Friends> friends = friendsRepository.findAllByToUser(currentMember);

        for(Friends f: friends){
            if(!f.isFriends()) continue;
            Members friend = memberRepository.findMembersByMemberIdx(f.getFromUser());
            List<Projects> projects = projectsRepository.findAllByMembers(friend);
            for(Projects p: projects){
                result.add(toResponse(p));
            }
        }
        return result;
    }

    public ProjectDetailResponse getProjectDetail(long idx){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        Projects tempProject = projectsRepository.findProjectsByIdx(idx);
        Members members = tempProject.getMembers();

        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(members, currentMember.getMemberIdx());

        if(!members.equals(currentMember) &&(friends == null || !friends.isFriends()))
            throw new FriendsException(ExceptionCode.NOT_FRIENDS);

        Projects projects = projectsRepository.findProjectsByIdx(idx);
        return toDetailResponse(projects);
    }

    @Transactional
    public void deleteProjects(long idx){
        Projects projects = projectsRepository.findProjectsByIdx(idx);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        if(!projects.getMembers().equals(currentMember))
            throw new ProjectException(ExceptionCode.PROJECT_OWNER);

        projectsRepository.delete(projects);

    }

    @Transactional
    public String changeImage(long idx, MultipartFile file){
        String url;
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
            throw new UtilException(ExceptionCode.NOT_IMAGE_FORMAT);
        }
        UUID uuid = UUID.randomUUID();
        try {
            String fileName = uuid + file.getOriginalFilename();
            String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/projects/" + fileName;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            url = fileUrl;
            amazonS3Client.putObject(bucket +"/projects", fileName, file.getInputStream(), objectMetadata);
        } catch (Exception e){
            throw new ProjectException(null);
        }
        Projects projects = projectsRepository.findProjectsByIdx(idx);
        String prevImg = projects.getImg();
        projects.setImg(url);

        try{
            System.out.println(prevImg);
            System.out.println("name" + prevImg.substring(prevImg.indexOf("/projects/")+10));
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket + "/projects", prevImg.substring(prevImg.indexOf("/projects/")+10)));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        projectsRepository.save(projects);
        return url;
    }

}
