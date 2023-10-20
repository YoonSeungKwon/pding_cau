package yoon.capstone.application.vo.request;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ProjectDto {

    private String name;

    private String info;

    private int goal;

    private LocalDate enddate;
}
