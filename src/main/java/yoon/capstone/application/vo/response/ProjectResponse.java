package yoon.capstone.application.vo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProjectResponse {

    private String owner;

    private String name;

    private String info;

    private int goal;

    private int curr;

    private LocalDateTime enddate;

}


