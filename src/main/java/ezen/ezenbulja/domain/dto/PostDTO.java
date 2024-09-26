package ezen.ezenbulja.domain.dto;

import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.domain.dao.Post;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDTO {

    private Long id;  // 게시글 ID

    @NotEmpty(message = "제목은 필수 항목입니다.")
    @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하로 입력해주세요.")
    private String title;  // 게시글 제목

    @NotEmpty(message = "내용은 필수 항목입니다.")
    @Size(min = 1, max = 255, message = "내용은 최소 1자 이상 입력해야 합니다.")
    private String content;  // 게시글 내용

    private Member author;  // 작성자 정보

    private int commentCount;  // 댓글 수 필드

    private LocalDateTime createdAt;  // 게시글 작성일 필드 추가

    // 기본 생성자
    public PostDTO() {}

    // Post 객체로부터 PostDTO로 변환하는 생성자
    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor();
        this.commentCount = 0;  // 댓글 수는 기본값 0으로 설정
        this.createdAt = post.getCreatedAt();  // 작성일 설정
    }

    // 댓글 수를 포함한 생성자
    public PostDTO(Post post, int commentCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor();
        this.commentCount = commentCount;  // 댓글 수 설정
        this.createdAt = post.getCreatedAt();  // 작성일 설정
    }
}
