package yoon.capstone.application.service.manager.mock;

import yoon.capstone.application.common.util.AesEncryptor;

public class MockAesManager implements AesEncryptor {
    @Override
    public String decode(String s) {
        return s;
    }

    @Override
    public String encode(String s) {
        return s;
    }
}
