package yoon.capstone.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "멤버 관련 API", description = "v1")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check/{email}")
    @Operation(summary = "이메일 중복 체크", description = "회원가입시 이메일 중복 여부를 확인해서 boolean 값으로 전달")
    public ResponseEntity<Boolean> emailDuplicationCheck(@PathVariable String email){
        return new ResponseEntity<>(memberService.existUser(email), HttpStatus.OK);
    }

    @PostMapping("/")
    @Operation(summary = "회원가입", description = "들어온 RegisterDto의 유효성을 검증하고 회원가입 진행")
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저 회원가입 정보", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterDto.class)))
            @Parameter(description = "User information", required = true)
            @RequestBody @Validated(RegisterValidationSequence.class) RegisterDto dto){

        MemberResponse result = memberService.formRegister(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    @Operation(summary = "유저 검색", description = "친구 신청을 위하여 email을 통하여 유저 검색, 만약 소셜 계정과 폼 계정이 중복되는 계정이 있으면 두 개다 반환")
    public ResponseEntity<List<MemberResponse>> findMemberByEmail(@PathVariable String email){
        return new ResponseEntity<>(memberService.findMember(email), HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "LoginDto의 유효성 검증 후 로그인 프로세스를 진행")
    public ResponseEntity<MemberResponse> login(
            @RequestBody @Validated(LoginValidationSequence.class) LoginDto dto, HttpServletResponse response){

        MemberResponse result = memberService.formLogin(dto, response);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "유저 테이블에서 저장된 Refresh Token을 제거")
    public ResponseEntity<String> logout(){
        memberService.logOut();
        return new ResponseEntity<>("로그아웃 되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/profile")    //프로필 이미지 변경
    @Operation(summary = "유저 프로필 사진 변경", description = "File 형식으로 받아온 이미지를 유효성 검사 후 스토리지 서버에 저장")
    public ResponseEntity<String> uploadProfileImage(@RequestBody MultipartFile file){
        String url = memberService.uploadProfile(file);

        return new ResponseEntity<>(url, HttpStatus.OK);
    }

}
