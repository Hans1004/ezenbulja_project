package ezen.ezenbulja.domain.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    // 작성자 정보 (Many-to-One 관계 설정)
    @ManyToOne
    @JoinColumn(name = "author_id")  // 외래 키 설정
    private Member author;

    // createdAt을 포맷팅해서 반환하는 getter 메서드 추가
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.createdAt != null ? this.createdAt.format(formatter) : "";
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;  // 게시글에 달린 댓글 리스트, 댓글도 같이 삭제 하기 위한

}
