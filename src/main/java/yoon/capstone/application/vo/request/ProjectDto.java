package yoon.capstone.application.vo.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import yoon.capstone.application.enums.ErrorCode;
import yoon.capstone.application.exception.sequence.ValidationGroup;

import java.time.LocalDate;

@Getter
public class ProjectDto {

    @NotBlank(message = "TITLE_NOT_BLANK", groups = ValidationGroup.TitleBlank.class)
    private String title;

    private String content;

    private String category;

    private String option;

    @NotBlank(message = "LINK_NOT_BLANK", groups = ValidationGroup.LinkBlank.class)
    private String link;

    @Positive(message = "GOAL_NOT_BLANK", groups = ValidationGroup.GoalBlank.class)
    private int goal;

    @Future(message = "DATE_NOT_FUTURE", groups = ValidationGroup.DateFuture.class)
    private LocalDate enddate;
}
