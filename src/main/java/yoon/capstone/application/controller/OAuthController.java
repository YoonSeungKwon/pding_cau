package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth2/code")
public class OAuthController {

    @Value("${kakao.oauth.key}")
    private String kakaoKey;

    @Value("${kakao.oauth.uri}")
    private String kakaoUri;

    @GetMapping("/kakao")
    public ResponseEntity<Map<String, String>> kakaoAPI(){
        Map<String, String> map = new HashMap<>();
        map.put("key", kakaoKey);
        map.put("uri", kakaoUri);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/kakao")
    public String kakaoLogin(@RequestBody String token){
        return "Login Success Token: " + token;
    }

}
