package ezen.ezenbulja.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginForm {
    
    @NotEmpty(message = "넣으라고")
    private String loginId;
    
    @NotEmpty(message = "넣으라고")
    private String password;


}
