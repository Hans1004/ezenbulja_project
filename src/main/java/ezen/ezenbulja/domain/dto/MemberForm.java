package ezen.ezenbulja.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

    private Long id;

    @NotEmpty(message = "넣으라고")
    private String loginId;
    @NotEmpty(message = "넣으라고")
    private String name;
    @NotEmpty(message = "넣으라고")
    private String password;

    private String grade;

}
