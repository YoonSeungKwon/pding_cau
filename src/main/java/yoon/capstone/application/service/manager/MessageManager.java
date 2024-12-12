package yoon.capstone.application.service.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface MessageManager {

    void publish(Object o);

    void subscribe();

}
