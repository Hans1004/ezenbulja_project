package ezen.ezenbulja.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

    private Long id;

    @NotEmpty(message = "ID를 입력해주세요.")
    private String loginId;
    @NotEmpty(message = "이름을 입력해주세요")
    private String name;
    @NotEmpty(message = "password를 입력해주세요.")
    private String password;

    private String grade;

}
