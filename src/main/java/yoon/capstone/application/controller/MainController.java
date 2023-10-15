package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yoon.capstone.application.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MainController {

    private final MemberService memberService;

    @GetMapping("/")
    public String mainPage(){
        return "Hello World!";
    }

}
