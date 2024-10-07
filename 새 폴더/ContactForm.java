package ezen.ezenbulja.domain.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Getter @Setter
public class ContactForm {

    @NotEmpty(message = "이름은 필수 항목입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotEmpty(message = "문의 사항을 작성해주세요.")
    private String content;

    private boolean news; // 뉴스레터 수신 여부
}
