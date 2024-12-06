package yoon.capstone.application.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;


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

    private String regdate;

    private String enddate;

}
