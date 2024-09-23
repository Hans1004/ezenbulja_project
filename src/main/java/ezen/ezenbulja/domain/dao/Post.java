package ezen.ezenbulja.domain.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Post.java (Entity)
@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "제목은 필수 항목입니다.")
    @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하로 입력해주세요.")
    private String title;

    @NotEmpty(message = "내용은 필수 항목입니다.")
    @Size(min = 1, max = 255, message = "내용은 최소 1자 이상 입력해야 합니다.")
    private String content;

    private LocalDateTime createdAt;

    // createdAt을 포맷팅해서 반환하는 getter 메서드 추가
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.createdAt != null ? this.createdAt.format(formatter) : "";
    }
}
