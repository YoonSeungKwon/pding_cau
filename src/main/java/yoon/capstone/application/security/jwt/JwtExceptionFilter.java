package yoon.capstone.application.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import yoon.capstone.application.enums.ErrorCode;
import yoon.capstone.application.vo.response.ErrorResponse;

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
        ErrorResponse errorResponse = new ErrorResponse();

        if(e.getMessage().equals(ErrorCode.ACCESS_TOKEN_EXPIRED.getStatus())){
            errorResponse.setCode(HttpStatus.UNAUTHORIZED);
            errorResponse.setStatus(ErrorCode.ACCESS_TOKEN_EXPIRED.getStatus());
            errorResponse.setMessage(ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage());
        }
        else if(e.getMessage().equals(ErrorCode.REFRESH_TOKEN_EXPIRED.getStatus())){
            errorResponse.setCode(HttpStatus.UNAUTHORIZED);
            errorResponse.setStatus(ErrorCode.REFRESH_TOKEN_EXPIRED.getStatus());
            errorResponse.setMessage(ErrorCode.REFRESH_TOKEN_EXPIRED.getMessage());
        }
        else{
            errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponse.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
            errorResponse.setMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        }
        response.setStatus(401);
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}