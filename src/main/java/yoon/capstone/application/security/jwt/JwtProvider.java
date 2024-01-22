package yoon.capstone.application.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.repository.MemberRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Lazy
@RequiredArgsConstructor
public class JwtProvider {

    private final MemberRepository memberRepository;
    private final long accExp = 60 * 60 * 1000l;
    private final long refExp = 6 * 60 * 60 * 1000l;

    @Value("${jwt.secret}")
    private String SECRET;

    public SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private String findIdByToken(String token){
        return memberRepository.findMembersByRefreshToken(token).getEmail();
    }
    public String createAccessToken(String id){

        Claims claims = Jwts.claims()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accExp));

        return  Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .claim("username", id)
                .signWith(getKey())
                .compact();
    }

    public String createRefreshToken(){

        Claims claims = Jwts.claims()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refExp));

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
        Members members = memberRepository.findMembersByEmail(getId(token));
        return new UsernamePasswordAuthenticationToken(members, null, members.getAuthority());
    }

    public String getId(String token){
        return (String)Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token)
                .getBody().get("username");
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