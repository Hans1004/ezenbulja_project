package ezen.ezenbulja.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class AnswerForm {
    private Long id;

    private String subject;
    private String answer;
    @NotEmpty
    private String content;
    private LocalDateTime modifyDate;
    private LocalDateTime createDate;
    private String author;
}
