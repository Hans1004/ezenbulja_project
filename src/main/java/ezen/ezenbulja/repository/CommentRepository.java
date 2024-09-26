package ezen.ezenbulja.repository;

import ezen.ezenbulja.domain.dao.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 모든 댓글을 조회
    List<Comment> findAllByPostId(Long postId);
    // 게시글 ID로 댓글 수를 세는 쿼리 메서드
    int countByPostId(Long postId);
}
