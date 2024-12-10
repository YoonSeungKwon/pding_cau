package yoon.capstone.application.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.common.annotation.Authenticated;
import yoon.capstone.application.common.dto.request.LoginDto;
import yoon.capstone.application.common.dto.request.OAuthDto;
import yoon.capstone.application.common.dto.request.RegisterDto;
import yoon.capstone.application.common.dto.response.MemberResponse;
import yoon.capstone.application.common.util.AesEncryptorManager;
import yoon.capstone.application.common.util.EmailFormatManager;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.enums.Provider;
import yoon.capstone.application.common.enums.Role;
import yoon.capstone.application.common.exception.UnauthorizedException;
import yoon.capstone.application.infrastructure.jpa.MemberJpaRepository;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.config.security.JwtProvider;
import yoon.capstone.application.service.manager.ProfileManager;
import yoon.capstone.application.service.manager.TokenRefreshTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@Builder
@RequiredArgsConstructor
public class MemberService {

    private final TokenRefreshTemplate tokenRefreshTemplate;

    private final MemberJpaRepository memberRepository;

    private final AesEncryptorManager aesEncryptorManager;

    private final ProfileManager profileManager;

    private final String DEFAULT_PROFILE = "https://pding-storage.s3.ap-northeast-2.amazonaws.com/members/icon.png";

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    private MemberResponse toResponse(Members members){
        return new MemberResponse(members.getMemberIdx(), EmailFormatManager.toEmail(members.getEmail())
                , members.getUsername(), aesEncryptorManager.decode(members.getPhone()), members.getProfile(), members.isOauth(), members.getLastVisit());
    }




    @Transactional(readOnly = true)
    public boolean existUser(String email){
        return memberRepository.existsByEmail(EmailFormatManager.toPersist(email, Provider.DEFAULT));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findMember(String email){

        //Lazy Loading
        List<Members> result = memberRepository.findMembersByEmailLikeString(email);

        return result.stream().map(this::toResponse).toList();
    }


    @Transactional
    public MemberResponse formLogin(LoginDto dto, HttpServletResponse response){

        String email = EmailFormatManager.toPersist(dto.getEmail(), Provider.DEFAULT);
        String password = dto.getPassword();

        //Lazy Loading
        Members members = memberRepository.findMembersByEmail(email).orElseThrow(()->new UsernameNotFoundException(email));

        if(!passwordEncoder.matches(password, members.getPassword()))
            throw new BadCredentialsException(email);

        String refToken = tokenRefreshTemplate.refreshToken(response, members);
        members.refresh(refToken);

        return toResponse(memberRepository.save(members));
    }

    @Transactional
    public MemberResponse formRegister(RegisterDto dto){

        Members members = Members.builder()
                .email(EmailFormatManager.toPersist(dto.getEmail(), Provider.DEFAULT))
                .password(passwordEncoder.encode(dto.getPassword()))
                .username(dto.getName())
                .profile(DEFAULT_PROFILE)
                .role(Role.USER)
                .oauth(false)
                .provider(Provider.DEFAULT)
                .build();

        if(dto.getPhone() != null && !dto.getPhone().equals("")){
            members.setPhone(aesEncryptorManager.encode(dto.getPhone()));
        }

        return toResponse(memberRepository.save(members));
    }

    @Transactional
    public void socialRegister(OAuthDto dto){

        Members members = Members.builder()
                .email(dto.getEmail()+"?"+Provider.KAKAO.getProvider())
                .username(dto.getName())
                .password("kakao_member")
                .profile(dto.getImage())
                .role(Role.USER)
                .oauth(true)
                .provider(Provider.KAKAO)
                .build();

        toResponse(memberRepository.save(members));
    }

    @Transactional
    public MemberResponse socialLogin(String email, HttpServletResponse response){

        Members members = memberRepository.findMembersByEmail(EmailFormatManager.toPersist(email, Provider.KAKAO))
                .orElseThrow(()->new UsernameNotFoundException(email));

        String refToken = tokenRefreshTemplate.refreshToken(response, members);
        members.refresh(refToken);

        return toResponse(memberRepository.save(members));
    }

    @Transactional
    @Authenticated
    public void logOut(){
        JwtAuthentication dto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Members members = memberRepository.findMembersByMemberIdx(dto.getMemberIdx()).orElseThrow(()-> new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

        members.logout();

        memberRepository.save(members);
    }

    @Transactional
    @Authenticated
    public MemberResponse uploadProfile(MultipartFile file){
        JwtAuthentication dto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Members currentMember = memberRepository.findMembersByMemberIdx(dto.getMemberIdx()).orElseThrow(()-> new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

        currentMember.setProfile(profileManager.updateProfile(file, currentMember.getMemberIdx()));
        return toResponse(memberRepository.save(currentMember));
    }
}
