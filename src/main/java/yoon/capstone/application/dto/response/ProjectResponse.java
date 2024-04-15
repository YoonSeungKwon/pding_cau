package yoon.capstone.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProjectResponse {

    private long idx;

    private String writer;

    private String title;

    private String img;

    private int goal;

    private int curr;

    private int count;

    private String category;

    private LocalDateTime enddate;

    private String profile;

}


