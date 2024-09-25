package ezen.ezenbulja.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginForm {
    
    @NotEmpty(message = "ID를 입력해주세요.")
    private String loginId;
    
    @NotEmpty(message = "password를 입력해주세요.")
    private String password;


}
