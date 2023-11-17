package yoon.capstone.application.exception.sequence;

import jakarta.validation.GroupSequence;

@GroupSequence({
        ValidationGroup.TitleBlank.class,
        ValidationGroup.LinkBlank.class,
        ValidationGroup.GoalBlank.class,
        ValidationGroup.DateBlank.class
})
public interface ProjectValidationSequence {
}
