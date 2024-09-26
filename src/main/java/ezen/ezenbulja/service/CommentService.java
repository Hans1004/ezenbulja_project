package ezen.ezenbulja.service;

import ezen.ezenbulja.domain.dao.Comment;
import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.domain.dao.Post;
import ezen.ezenbulja.domain.dto.CommentDTO;
import ezen.ezenbulja.repository.CommentRepository;
import ezen.ezenbulja.repository.MemberRepository;
import ezen.ezenbulja.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;  // 작성자 정보를 가져오기 위한 MemberRepository 추가

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    // 댓글 저장 메서드
    public Comment addComment(CommentDTO commentDTO, Long postId, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        Member author = memberRepository.findById(authorId)  // 작성자의 Member 객체를 조회
                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);  // 댓글이 작성된 게시글 설정
        comment.setAuthor(author);  // 댓글 작성자 설정 (authorId 대신 Member 객체 설정)

        return commentRepository.save(comment);
    }

    // 특정 게시글의 댓글 조회
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);  // 댓글 ID로 바로 삭제
    }


    // 댓글 작성자 확인
    public boolean isAuthorOfComment(Long commentId, Long authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        return comment.getAuthor().getId().equals(authorId);  // authorId 대신 Member 객체의 ID를 비교
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }
    // 댓글 ID로 댓글을 조회하는 메서드
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
    }
    // 댓글 수 카운트
    public int getCommentCountByPostId(Long postId) {
        return commentRepository.countByPostId(postId);  // 이미 존재하는 메서드 활용
    }
}
