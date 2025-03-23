package yoon.capstone.application.service.manager.stub;

import org.springframework.security.crypto.password.PasswordEncoder;

public class StubPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return true;
    }
}
