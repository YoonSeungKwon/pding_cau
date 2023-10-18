package yoon.capstone.application.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch(JwtException e){

        }
    }

    public void setErrorResponse(HttpServletResponse response, JwtException e) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
//        ErrorResponse errorResponse = new ErrorResponse();
//        Errors errors;

//        if(e.getMessage().equals(Errors.ACCESS_TOKEN_EXPIRED.getCode())){
//            errors = Errors.ACCESS_TOKEN_EXPIRED;
//        }
//        else if(e.getMessage().equals(Errors.REFRESH_TOKEN_EXPIRED.getCode())){
//            errors = Errors.REFRESH_TOKEN_EXPIRED;
//        }
//        else{
//            errors = Errors.Internal_Server_Error;
//        }
//
//        errorResponse.setCode(errors.getCode());
//        errorResponse.setMessage(errors.getMessage());
//
//        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}