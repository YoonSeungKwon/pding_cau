package yoon.capstone.application.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AesEncryptorManager {

    private final AesBytesEncryptor aesBytesEncryptor;

    public String encode(String s){
        byte[] encrypt = aesBytesEncryptor.encrypt(s.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypt);
    }


    public String decode(String s){
        byte[] encrypt = Base64.getDecoder().decode(s);
        return new String(aesBytesEncryptor.decrypt(encrypt), StandardCharsets.UTF_8);
    }



}
