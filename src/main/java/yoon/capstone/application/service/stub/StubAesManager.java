package yoon.capstone.application.service.stub;

import yoon.capstone.application.common.util.AesEncryptor;

public class StubAesManager implements AesEncryptor {
    @Override
    public String decode(String s) {
        return s;
    }

    @Override
    public String encode(String s) {
        return s;
    }
}
