package ezen.ezenbulja.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// PostDTO.java
@Getter
@Setter
public class PostDTO {
    @NotEmpty
    @Size(min = 2, max = 100)
    private String title;

    @NotEmpty
    @Size(min = 1, max = 255)
    private String content;

    // getter, setter
}
