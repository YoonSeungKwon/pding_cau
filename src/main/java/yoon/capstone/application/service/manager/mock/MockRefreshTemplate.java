package yoon.capstone.application.service.manager.mock;

import jakarta.servlet.http.HttpServletResponse;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.manager.RefreshTemplate;

public class MockRefreshTemplate implements RefreshTemplate {
    @Override
    public String refreshToken(HttpServletResponse response, Members members) {
        return "refreshToken";
    }
}
