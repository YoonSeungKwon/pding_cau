package yoon.capstone.application.vo.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ProjectDto {

    private String title;

    private String content;

    private String category;

    private String option;

    private String link;

    private int goal;

    private LocalDate enddate;
}
