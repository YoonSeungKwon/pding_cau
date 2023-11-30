package yoon.capstone.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.domain.Friends;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.domain.Projects;
import yoon.capstone.application.enums.Categorys;
import yoon.capstone.application.enums.ErrorCode;
import yoon.capstone.application.exception.FriendsException;
import yoon.capstone.application.exception.ProjectException;
import yoon.capstone.application.exception.UtilException;
import yoon.capstone.application.repository.FriendsRepository;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.vo.request.ProjectDto;
import yoon.capstone.application.vo.response.ProjectDetailResponse;
import yoon.capstone.application.vo.response.ProjectResponse;

import java.io.IOException;
import java.time.LocalDateTime;
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
    private final String region = "ap-northeast-2";


    private ProjectResponse toResponse(Projects projects){
        return new ProjectResponse(projects.getIdx(), projects.getMembers().getUsername(), projects.getTitle(), projects.getImg(), projects.getGoal(),
                projects.getCurr(), projects.getCategory().getValue(), projects.getEnddate());
    }

    private ProjectDetailResponse toDetailResponse(Projects projects){
        return new ProjectDetailResponse(projects.getTitle(), projects.getContent(), projects.getOption(),
                projects.getImg(), projects.getLink(), projects.getGoal(), projects.getCurr(), projects.getEnddate());
    }

    public ProjectResponse makeProjects(MultipartFile file, ProjectDto dto) {
        Members me = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String url;
        if (!file.getContentType().startsWith("image")) {
            throw new UtilException(ErrorCode.NOT_IMAGE_FORMAT.getStatus());
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
            throw new ProjectException(null);
        }
        Categorys categorys;
        if(dto.getCategory().equals(Categorys.생일.getValue())){
            categorys = Categorys.생일;
        }else{
            categorys = Categorys.졸업;
        }

        Projects projects = Projects.builder()
                .members(me)
                .title(dto.getTitle())
                .content(dto.getContent())
                .link(dto.getLink())
                .option(dto.getOption())
                .img(url)
                .goal(dto.getGoal())
                .enddate(dto.getEnddate())
                .category(categorys)
                .build();
        return toResponse(projectsRepository.save(projects));
    }
    public List<ProjectResponse> getProjectList(){
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ProjectResponse> result = new ArrayList<>();
        List<Projects> list = projectsRepository.findAllByMembers(members);

        for(Projects p:list){
            result.add(toResponse(p));
        }

        return result;
    }

    public List<ProjectResponse> getFriendsList(){
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ProjectResponse> result = new ArrayList<>();
        List<Friends> friends = friendsRepository.findAllByToUser(members);

        for(Friends f: friends){
            if(!f.isFriends()) continue;
            Members friend = memberRepository.findMembersByIdx(f.getFromUser());
            List<Projects> projects = projectsRepository.findAllByMembers(friend);
            for(Projects p: projects){
                result.add(toResponse(p));
            }
        }
        return result;
    }

    public ProjectDetailResponse getProjectDetail(long idx){
        Members me = (Members)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Projects tempProject = projectsRepository.findProjectsByIdx(idx);
        Members members = tempProject.getMembers();

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

    public String changeImage(long idx, MultipartFile file){
        String url;
        if (!file.getContentType().startsWith("image")) {
            throw new UtilException(ErrorCode.NOT_IMAGE_FORMAT.getStatus());
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
