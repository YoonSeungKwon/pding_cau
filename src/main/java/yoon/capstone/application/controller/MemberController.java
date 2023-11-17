package yoon.capstone.application.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.exception.sequence.LoginValidationSequence;
import yoon.capstone.application.exception.sequence.RegisterValidationSequence;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.vo.request.LoginDto;
import yoon.capstone.application.vo.request.RegisterDto;
import yoon.capstone.application.vo.response.MemberResponse;

import java.io.File;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check/{email}")
    public ResponseEntity<Boolean> emailDuplicationCheck(@PathVariable String email){
        return new ResponseEntity<>(memberService.existUser(email), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> register(@RequestBody @Validated(RegisterValidationSequence.class) RegisterDto dto){

        MemberResponse result = memberService.formRegister(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<MemberResponse>> findMemberByEmail(@PathVariable String email){
        return new ResponseEntity<>(memberService.findMember(email), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<MemberResponse> login(@RequestBody @Validated(LoginValidationSequence.class) LoginDto dto, HttpServletResponse response){

        MemberResponse result = memberService.formLogin(dto, response);

        //Authorization Header Config (JWT)

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(){
        memberService.logOut();
        return new ResponseEntity<>("로그아웃 되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/profile")    //프로필 이미지 변경
    public ResponseEntity<String> uploadProfileImage(@RequestBody MultipartFile file){
        String url = memberService.uploadProfile(file);

        return new ResponseEntity<>(url, HttpStatus.OK);
    }

}
