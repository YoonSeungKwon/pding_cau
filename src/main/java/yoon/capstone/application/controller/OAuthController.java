package yoon.capstone.application.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.vo.request.OAuthDto;
import yoon.capstone.application.vo.response.MemberResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth2/code")
public class OAuthController {

    private final MemberService memberService;

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
    public ResponseEntity<MemberResponse> kakaoLogin(@RequestBody String token, HttpServletResponse response) throws ParseException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity request = new HttpEntity(headers);
        String url = "https://kapi.kakao.com/v2/user/me";
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET,  request, String.class, 1);

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(result.getBody());
        JSONObject kakaoAccount = (JSONObject) object.get("kakao_account");
        JSONObject profile = (JSONObject) kakaoAccount.get("profile");
        String image = String.valueOf(profile.get("profile_image_url"));
        String nickname = String.valueOf(profile.get("nickname"));
        String email = String.valueOf(kakaoAccount.get("email"));

        OAuthDto dto = new OAuthDto(email, nickname, image);
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("name", nickname);

        if(!memberService.existUser(email)){
            memberService.socialRegister(dto);
        }
        memberService.socialLogin(email, response);
        MemberResponse memberResponse = new MemberResponse(email, nickname, image, true);

        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

}
