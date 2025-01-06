package yoon.capstone.application.config.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import yoon.capstone.application.service.repository.MemberRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider implements TokenProvider{

    private final MemberRepository memberRepository;

    @Value("${JWT_ACCESS}")
    private long accExp;
    @Value("${JWT_REFRESH}")
    private long refExp;
    @Value("${JWT_SECRET}")
    private String SECRET;

    public SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private String findIdByToken(String token){
        return memberRepository.findAuthenticationWithToken(token).getEmail();
    }

    @Override
    public String createAccessToken(String email){

        Claims claims = Jwts.claims()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accExp * 60 * 1000l * 100000));

        return  Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .claim("email", email)
                .signWith(getKey())
                .compact();
    }

    @Override
    public String createRefreshToken(){

        Claims claims = Jwts.claims()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refExp * 60 * 60 * 1000l));

        return  Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .signWith(getKey())
                .compact();
    }

    public String createNewToken(String ref_token){
        return createAccessToken(findIdByToken(ref_token));
    }

    public Authentication getAuthentication(String token){
        JwtAuthentication dto  = memberRepository.findAuthenticationWithEmail(getEmail(token));
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(dto.getRole().getRoleKey()));
        return new JwtAuthenticationToken(dto, null, authorities);
    }

    public String getEmail(String token){
        return (String)Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token)
                .getBody().get("email");
    }

    public String resolveAccessToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith("Bearer"))
            return token.substring(7);
        else return null;
    }

    public String resolveRefreshToken(HttpServletRequest request){
        String token = request.getHeader("X-Refresh-Token");
        if(StringUtils.hasText(token) && token.startsWith("Bearer"))
            return token.substring(7);
        else return null;
    }

    public boolean validateToken(String token){
        try{
            Claims claims = Jwts.parserBuilder().setSigningKey(getKey()).build()
                    .parseClaimsJws(token).getBody();
            return !claims.getExpiration().before(new Date());
        }catch(Exception e){
            return false;
        }
    }

}