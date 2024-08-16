package yoon.capstone.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProjectCache {

    private long projectIdx;

    private String title;

    private int currentAmount;

    private int goalAmount;

    private int participantsCount;

}
