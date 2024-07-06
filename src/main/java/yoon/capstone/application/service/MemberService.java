package yoon.capstone.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.enums.Role;
import yoon.capstone.application.exception.ProjectException;
import yoon.capstone.application.exception.UnauthorizedException;
import yoon.capstone.application.exception.UtilException;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.security.JwtProvider;
import yoon.capstone.application.dto.request.LoginDto;
import yoon.capstone.application.dto.request.OAuthDto;
import yoon.capstone.application.dto.request.RegisterDto;
import yoon.capstone.application.dto.response.MemberResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final AmazonS3Client amazonS3Client;

    private final String bucket = "cau-artech-capstone";
    private final String region = "ap-northeast-2";


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private MemberResponse toResponse(Members members){
        return new MemberResponse(members.getEmail(), members.getUsername(), members.getProfile(), members.isOauth(), members.getLastVisit());
    }

    public boolean existUser(String email){
        return memberRepository.existsByEmail(email);
    }

    public List<MemberResponse> findMember(String email){

        List<Members> result = memberRepository.findAllByEmail(email);
        List<MemberResponse> response = new ArrayList<>();

        for(Members m:result){
            response.add(toResponse(m));
        }

        return response;
    }

    @Transactional
    public MemberResponse formLogin(LoginDto dto, HttpServletResponse response){

        String email = dto.getEmail();
        String password = dto.getPassword();
        Members members = memberRepository.findMembersByEmail(email);

        if(members == null || members.isOauth())
            throw new UsernameNotFoundException(email);
        if(!passwordEncoder.matches(password, members.getPassword()))
            throw new BadCredentialsException(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(members,
                null, members.getAuthority());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accToken = jwtProvider.createAccessToken(members.getEmail());
        String refToken = jwtProvider.createRefreshToken();
        members.setRefreshToken(refToken);
        members.setLastVisit(LocalDateTime.now());
        response.setHeader("Authorization", accToken);
        response.setHeader("X-Refresh-Token", refToken);

        return toResponse(memberRepository.save(members));
    }

    @Transactional
    public MemberResponse formRegister(RegisterDto dto){

        Members members = Members.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .username(dto.getName())
                .profile("https://pding-storage.s3.ap-northeast-2.amazonaws.com/members/icon.png")
                .role(Role.USER)
                .oauth(false)
                .build();
        memberRepository.save(members);

        return toResponse(members);
    }

    @Transactional
    public void socialRegister(OAuthDto dto){

        Members members = Members.builder()
                .email(dto.getEmail())
                .username(dto.getName())
                .password("kakao_member")
                .profile(dto.getImage())
                .role(Role.USER)
                .oauth(true)
                .build();

        memberRepository.save(members);


        toResponse(members);
    }

    @Transactional
    public MemberResponse socialLogin(String email, HttpServletResponse response){
        Members members = memberRepository.findMembersByEmail(email);

        String accToken = jwtProvider.createAccessToken(members.getEmail());
        String refToken = jwtProvider.createRefreshToken();
        members.setRefreshToken(refToken);
        members.setLastVisit(LocalDateTime.now());
        response.setHeader("Authorization", accToken);
        response.setHeader("X-Refresh-Token", refToken);

        memberRepository.save(members);
        return toResponse(members);
    }

    @Transactional
    public void logOut(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();
        currentMember.setRefreshToken(null);
        memberRepository.save(currentMember);
    }

    @Transactional
    public String uploadProfile(MultipartFile file){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();
        String url;
        UUID uuid = UUID.randomUUID();
        if (!file.getContentType().startsWith("image")) {
            throw new UtilException(ExceptionCode.NOT_IMAGE_FORMAT);
        }
        try {
            String fileName = uuid + file.getOriginalFilename();
            String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/members/" + currentMember.getMemberIdx() + "/" + fileName;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            System.out.println(file.getContentType());
            url = fileUrl;
            amazonS3Client.putObject(bucket +"/members/" + currentMember.getMemberIdx(), fileName, file.getInputStream(), objectMetadata);
        } catch (Exception e){
            throw new ProjectException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
        currentMember.setProfile(url);
        memberRepository.save(currentMember);
        return url;
    }
}
