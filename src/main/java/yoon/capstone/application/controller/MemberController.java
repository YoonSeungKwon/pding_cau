package yoon.capstone.application.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import yoon.capstone.application.exception.sequence.LoginValidationSequence;
import yoon.capstone.application.exception.sequence.RegisterValidationSequence;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.vo.request.LoginDto;
import yoon.capstone.application.vo.request.RegisterDto;
import yoon.capstone.application.vo.response.MemberResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/")
    public ResponseEntity<?> register(@RequestBody @Validated(RegisterValidationSequence.class) RegisterDto dto){

        MemberResponse result = memberService.formRegister(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated(LoginValidationSequence.class) LoginDto dto, HttpServletResponse response){

        MemberResponse result = memberService.formLogin(dto, response);

        //Authorization Header Config (JWT)

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
