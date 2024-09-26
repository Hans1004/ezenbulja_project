package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dao.Comment;
import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.domain.dao.Post;
import ezen.ezenbulja.domain.dto.CommentDTO;
import ezen.ezenbulja.service.CommentService;
import ezen.ezenbulja.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/posts/{postId}/comments")  // 경로를 게시글 하위로 변경
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    // 댓글 작성 처리
    @PostMapping
    public String addComment(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             @ModelAttribute("commentDTO") @Valid CommentDTO commentDTO,
                             BindingResult result,
                             @PathVariable("postId") Long postId,  // @PathVariable로 수정
                             Model model) {

        logger.info("Received request to add comment for postId: {}", postId);  // 로그 추가
        logger.info("Comment content: {}", commentDTO.getContent());  // 댓글 내용 로그 출력

        if (loginMember == null) {
            logger.warn("User not logged in. Redirecting to login page.");  // 로그인 확인 로그
            return "redirect:/login";  // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        if (result.hasErrors()) {
            logger.warn("Comment validation failed. Returning to post view with errors.");  // 유효성 검증 실패 로그
            // 유효성 검증 실패 시 다시 게시글 상세 페이지로 이동하고, 에러 메시지를 전달
            model.addAttribute("post", postService.getPostById(postId));  // postService 사용
            model.addAttribute("comments", commentService.getCommentsByPostId(postId));
            model.addAttribute("errorMessage", "댓글 내용을 입력해 주세요.");
            return "user/view";  // 다시 게시글 상세 페이지로 이동
        }

        // 댓글 작성
        logger.info("Adding comment for postId: {} by userId: {}", postId, loginMember.getId());  // 댓글 작성 로그
        commentService.addComment(commentDTO, postId, loginMember.getId());

        logger.info("Comment added successfully. Redirecting to post details page.");  // 댓글 작성 성공 로그
        return "redirect:/posts/" + postId;  // 댓글 작성 후 게시글 상세 페이지로 리다이렉트
    }

    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                @PathVariable Long postId,
                                @PathVariable Long commentId) {
        // 로그 메시지 추가: 댓글 삭제 요청이 들어왔을 때 postId와 commentId를 기록
        logger.info("Received DELETE request for postId: {}, commentId: {}", postId, commentId);

        if (loginMember == null) {
            return "redirect:/login";
        }

        // 댓글을 가져와서 해당 댓글이 해당 게시글에 속하는지 확인
        Comment comment = commentService.getCommentById(commentId);
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글에 속하지 않는 댓글입니다.");
        }

        // 댓글 작성자와 로그인된 사용자가 다르면 권한 없음 예외 처리
        if (!comment.getAuthor().getId().equals(loginMember.getId())) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        // 댓글 삭제 로직 실행
        commentService.deleteComment(commentId);
        return "redirect:/posts/";
    }

}
