package yoon.capstone.application.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import yoon.capstone.application.domain.Carts;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.enums.Role;
import yoon.capstone.application.repository.CartRepository;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.security.jwt.JwtProvider;
import yoon.capstone.application.vo.request.LoginDto;
import yoon.capstone.application.vo.request.OAuthDto;
import yoon.capstone.application.vo.request.RegisterDto;
import yoon.capstone.application.vo.response.MemberDetailResponse;
import yoon.capstone.application.vo.response.MemberResponse;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private MemberResponse toResponse(Members members){
        return new MemberResponse(members.getEmail(), members.getUsername());
    }

    public boolean existUser(String email){
        return memberRepository.existsByEmail(email);
    }

    public MemberDetailResponse memberDetail(String email){
        Members members = memberRepository.findMembersByEmail(email);
        return new MemberDetailResponse(members.getEmail(), members.getUsername(), members.isOauth(),
                members.getRegdate(), members.getLastVisit(), members.getPhone());
    }

    public MemberResponse formLogin(LoginDto dto, HttpServletResponse response){

        String email = dto.getEmail();
        String password = dto.getPassword();
        Members members = memberRepository.findMembersByEmail(email);

        if(members == null)
            throw new UsernameNotFoundException(email);
        if(!passwordEncoder.matches(password, members.getPassword()))
            throw new BadCredentialsException(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(members,
                null, members.getAuthority());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accToken = jwtProvider.createAccessToken(members.getEmail());
        String refToken = jwtProvider.createRefreshToken();
        members.setRefresh_token(refToken);
        members.setLastVisit(LocalDateTime.now());
        response.setHeader("Authorization", accToken);
        response.setHeader("X-Refresh-Token", refToken);

        return toResponse(memberRepository.save(members));
    }

    public MemberResponse formRegister(RegisterDto dto){

        Members members = Members.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .username(dto.getName())
                .role(Role.USER)
                .oauth(false)
                .build();
        memberRepository.save(members);

        Carts carts = Carts.builder()
                .members(members)
                .build();
        cartRepository.save(carts);

        return toResponse(members);
    }

    public void socialRegister(OAuthDto dto){

        Members members = Members.builder()
                .email(dto.getEmail())
                .username(dto.getName())
                .password(null)
                .role(Role.USER)
                .oauth(true)
                .build();
        memberRepository.save(members);

        Carts carts = Carts.builder()
                .members(members)
                .build();
        cartRepository.save(carts);

        toResponse(members);
    }

    public void socialLogin(String email){
        Members members = memberRepository.findMembersByEmail(email);
        members.setLastVisit(LocalDateTime.now());
        memberRepository.save(members);
        return;
    }

    public void logOut(){
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication();
        members.setRefresh_token(null);
        memberRepository.save(members);
    }
}
