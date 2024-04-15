package yoon.capstone.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ProjectDetailResponse {

    private String title;

    private String content;

    private String writer;

    private String profile;

    private String option;

    private String category;

    private String img;

    private String link;

    private int goal;

    private int curr;

    private int count;

    private LocalDateTime regdate;

    private LocalDateTime enddate;

}
