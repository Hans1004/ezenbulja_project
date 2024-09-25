package ezen.ezenbulja.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

    private Long id;

    @NotEmpty(message = "ID를 입력해주세요.")
    private String loginId;

    @NotEmpty(message = "이름을 입력해주세요.")
    private String name;

    @NotEmpty(message = "현재 비밀번호를 입력해주세요.")
    private String password;  // 현재 비밀번호로 수정

    private String newPassword;  // 새 비밀번호

    private String confirmNewPassword;

    private String grade;
}
