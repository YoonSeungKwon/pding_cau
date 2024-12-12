package yoon.capstone.application.service.manager;

import org.springframework.stereotype.Service;
import yoon.capstone.application.common.dto.response.PayResponse;

@Service
public interface OrderManager {

    void orderCancel();

    PayResponse orderPrepare(long index, String name, String code, int total);

    void orderAccess(long index, String code, String tid, String token);

}
