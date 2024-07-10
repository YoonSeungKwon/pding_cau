package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yoon.capstone.application.dto.request.RegisterDto;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.exception.UnauthorizedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class MainController {

    @GetMapping("/")
    public String mainPage(){
        return "Hello World!";
    }

    @GetMapping("/user")
    public String userTestPage(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        return currentMember.getEmail();
    }

    @PostMapping("/test")
    public String dtoTest(@RequestBody RegisterDto dto){
        System.out.println(dto.getEmail());
        System.out.println(dto.getPassword());
        System.out.println(dto.getName());
        return "Email: " + dto.getEmail() + "\nPassword: " + dto.getPassword() + "\nName: " + dto.getName();
    }

}
