package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.enums.Role;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.vo.request.LoginDto;
import yoon.capstone.application.vo.request.OAuthDto;
import yoon.capstone.application.vo.request.RegisterDto;
import yoon.capstone.application.vo.response.MemberResponse;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private MemberResponse toResponse(Members members){
        return new MemberResponse(members.getEmail(), members.getUsername());
    }

    public boolean existUser(String email){
        return memberRepository.existsByEmail(email);
    }

    public MemberResponse formLogin(LoginDto dto){

        String email = dto.getEmail();
        String password = dto.getPassword();
        Members members = memberRepository.getMembersByEmail(email);

        if(members == null)
            throw new UsernameNotFoundException(email);
        if(!passwordEncoder.matches(password, members.getPassword()))
            throw new BadCredentialsException(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(members,
                null, members.getAuthority());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return toResponse(members);
    }

    public MemberResponse formRegister(RegisterDto dto){

        Members members = Members.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .username(dto.getName())
                .role(Role.USER)
                .oauth(false)
                .build();

        return toResponse(memberRepository.save(members));
    }

    public MemberResponse socialRegister(OAuthDto dto){
        Members members = Members.builder()
                .email(dto.getEmail())
                .username(dto.getName())
                .password(null)
                .role(Role.USER)
                .oauth(true)
                .build();

        return toResponse(memberRepository.save(members));
    }
}
