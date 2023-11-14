package yoon.capstone.application.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import yoon.capstone.application.enums.ErrorCode;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String acc_token = jwtProvider.resolveAccessToken(request);
        if(acc_token != null && jwtProvider.validateToken(acc_token)){
            Authentication authentication = jwtProvider.getAuthentication(acc_token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        else if(acc_token != null){
            String ref_token = jwtProvider.resolveRefreshToken(request);
            if(ref_token == null)
                throw new JwtException(ErrorCode.ACCESS_TOKEN_EXPIRED.getStatus());
            if(jwtProvider.validateToken(ref_token)){
                String new_token = jwtProvider.createNewToken(ref_token);
                Authentication authentication = jwtProvider.getAuthentication(new_token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                response.setHeader("Authorization", new_token);
            }
            else{
                throw new JwtException(ErrorCode.REFRESH_TOKEN_EXPIRED.getStatus());
            }
        }

        filterChain.doFilter(request, response);
    }
}