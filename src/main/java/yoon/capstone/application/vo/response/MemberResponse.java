package yoon.capstone.application.vo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.boot.jaxb.internal.stax.JpaOrmXmlEventReader;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MemberResponse {

    private String email;

    private String name;

    private String profile;

    private boolean oauth;

}
