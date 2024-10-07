package ezen.ezenbulja.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactForm {

    @NotEmpty(message = "이름은 필수 항목입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해 주세요.")
    private String email;

    @NotEmpty(message = "문의사항 작성은 필수 항목입니다.")
    @Size(min = 10, message = "문의사항은 최소 10자 이상이어야 합니다.")  // 최소 글자 수 제한 추가
    private String content;

    private boolean news;

    // loginId 필드 제거 (사용되지 않음)
}
