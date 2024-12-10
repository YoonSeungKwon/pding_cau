package yoon.capstone.application.service.manager;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import yoon.capstone.application.config.security.TokenProvider;
import yoon.capstone.application.service.domain.Members;

@Service
@RequiredArgsConstructor
public class TokenRefreshTemplate {

    private final TokenProvider tokenProvider;

    public String refreshToken(HttpServletResponse response, Members members){
        String accessToken = tokenProvider.createAccessToken(members.getEmail());
        String refreshToken = tokenProvider.createRefreshToken();

        response.setHeader("Authorization", accessToken);
        response.setHeader("X-Refresh-Token", refreshToken);


        return refreshToken;
    }


}
