package yoon.capstone.application.service.manager;

import jakarta.servlet.http.HttpServletResponse;
import yoon.capstone.application.service.domain.Members;

public interface RefreshTemplate {
    String refreshToken(HttpServletResponse response, Members members);

}
