package yoon.capstone.application.presentation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import yoon.capstone.application.common.dto.request.OAuthDto;
import yoon.capstone.application.common.dto.response.MemberResponse;
import yoon.capstone.application.service.MemberService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth2/code")
public class OAuthController {

    private final MemberService memberService;


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
        System.out.println(token);
        System.out.println(object);

        if(!memberService.existUser(email)){
            memberService.socialRegister(dto);
        }
        MemberResponse memberResponse = memberService.socialLogin(email, response);

        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

}
