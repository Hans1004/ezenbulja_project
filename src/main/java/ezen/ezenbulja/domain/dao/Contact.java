package ezen.ezenbulja.domain.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY 전략 명시
    private Long id;

    private Long userId; // 사용자 ID 필드 추가

    @NotEmpty(message = "이름은 필수 항목입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.") // 이메일 형식 검증
    private String email;

    @Column(columnDefinition = "TEXT")
    @NotEmpty(message = "문의사항 작성은 필수 항목입니다.")
    private String content;

    private boolean news; // 뉴스레터 수신 여부
}
