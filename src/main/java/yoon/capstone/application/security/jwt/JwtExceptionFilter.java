package yoon.capstone.application.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import yoon.capstone.application.enums.ExceptionCode;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch(JwtException e){
            setErrorResponse(response, e);
        }
    }

    public void setErrorResponse(HttpServletResponse response, JwtException e) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        ExceptionCode exceptionCode;

        if(e.getMessage().equals(ExceptionCode.ACCESS_TOKEN_EXPIRED.getMessage())){
            exceptionCode = ExceptionCode.ACCESS_TOKEN_EXPIRED;
        }
        else if(e.getMessage().equals(ExceptionCode.REFRESH_TOKEN_EXPIRED.getMessage())){
            exceptionCode = ExceptionCode.REFRESH_TOKEN_EXPIRED;
        }
        else{
            exceptionCode = ExceptionCode.INTERNAL_SERVER_ERROR;
        }
        response.setStatus(exceptionCode.getStatus().value());
        mapper.writeValue(response.getOutputStream(), exceptionCode.getMessage());
    }
}