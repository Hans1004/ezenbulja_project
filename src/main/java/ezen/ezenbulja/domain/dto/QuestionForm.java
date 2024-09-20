package ezen.ezenbulja.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QuestionForm {

    @NotEmpty(message = "넣어라")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "넣어라")
    private String content;

}
