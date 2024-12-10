package yoon.capstone.application.config.security;

public interface TokenProvider {

    String createAccessToken(String email);

    String createRefreshToken();

}
