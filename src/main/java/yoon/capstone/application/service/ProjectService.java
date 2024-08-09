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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.dto.request.ProjectDto;
import yoon.capstone.application.dto.response.ProjectDetailResponse;
import yoon.capstone.application.dto.response.ProjectResponse;
import yoon.capstone.application.entity.Friends;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.entity.Projects;
import yoon.capstone.application.enums.Category;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.exception.FriendsException;
import yoon.capstone.application.exception.ProjectException;
import yoon.capstone.application.exception.UnauthorizedException;
import yoon.capstone.application.exception.UtilException;
import yoon.capstone.application.repository.FriendsRepository;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.security.JwtAuthentication;

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
        return new ProjectResponse(projects.getProjectIdx(), projects.getMembers().getUsername(), projects.getTitle(), projects.getImage(), projects.getGoalAmount(),
                projects.getCurrentAmount(), projects.getParticipantsCount(), projects.getCategory().getValue(),
                projects.getCreatedAt().toString(), projects.getFinishAt().toString(), projects.getMembers().getProfile());
    }

    private ProjectDetailResponse toDetailResponse(Projects projects){
        return new ProjectDetailResponse(projects.getTitle(), projects.getContent(), projects.getMembers().getUsername(), projects.getMembers().getProfile(),
                projects.getOption(), projects.getCategory().getValue(), projects.getImage(), projects.getLink(), projects.getGoalAmount(), projects.getCurrentAmount()
                ,projects.getParticipantsCount(), projects.getCreatedAt().toString(), projects.getFinishAt().toString());
    }

    @CachePut(value = "myProjectList", key = "#cacheIndex")
    @Transactional
    public List<ProjectResponse> makeProjects(MultipartFile file, ProjectDto dto, long cacheIndex) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Eagle Loading
        Members currentMember = memberRepository.findMembersByMemberIdxWithFetchJoin(memberDto.getMemberIdx())
                .orElseThrow(()->new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

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
        Category category;
        if(dto.getCategory().equals(Category.생일.getValue())){
            category = Category.생일;
        }else{
            category = Category.졸업;
        }

        Projects projects = Projects.builder()
                .members(currentMember)
                .title(dto.getTitle())
                .content(dto.getContent())
                .link(dto.getLink())
                .option(dto.getOption())
                .image(url)
                .goal(dto.getGoal())
                .finishAt(dto.getEnddate())
                .category(category)
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
    @Cacheable(value = "myProjectList", key = "#cacheIndex")
    public List<ProjectResponse> getProjectList(long cacheIndex){

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

    @Cacheable(value = "projectListLatest", key = "#cacheIndex")
    public List<ProjectResponse> getFriendsListLatest(long cacheIndex){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Eagle Loading
        List<Projects> list = projectsRepository.findProjectsByFriendsFromUserOrderByLatest(memberDto.getMemberIdx());

        return list.stream().map((this::toResponse)).toList();
    }

    @Cacheable(value = "projectListUpcoming", key = "#cacheIndex")
    public List<ProjectResponse> getFriendsListUpcoming(long cacheIndex){

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
        //Lazy Loading
        Projects projects = projectsRepository.findProjectsByProjectIdx(idx);
        String prevImg = projects.getImage();
        projects.setImage(url);

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
