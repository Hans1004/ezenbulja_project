package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dao.Comment;
import ezen.ezenbulja.domain.dao.Post;
import ezen.ezenbulja.domain.dto.CommentDTO;
import ezen.ezenbulja.domain.dto.PostDTO;
import ezen.ezenbulja.repository.PostRepository;
import ezen.ezenbulja.service.CommentService;
import ezen.ezenbulja.service.PostService;
import ezen.ezenbulja.domain.dao.Member;  // 세션에서 로그인 정보를 확인하기 위해 추가
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final PostRepository postRepository;

    public PostController(PostService postService, CommentService commentService, PostRepository postRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.postRepository = postRepository;
    }

    @GetMapping("/posts")
    public String listPosts(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        if (loginMember == null) {
            return "redirect:/login";
        }

        List<Post> posts = postService.getAllPosts();  // 게시글 목록 조회 (작성자 정보 포함)

        // PostDTO로 변환하면서 댓글 수 포함
        List<PostDTO> postDTOs = posts.stream()
                .map(post -> {
                    int commentCount = commentService.getCommentCountByPostId(post.getId());  // 댓글 수 계산
                    return new PostDTO(post, commentCount);  // PostDTO로 변환
                })
                .collect(Collectors.toList());

        model.addAttribute("loginMember", loginMember);  // 로그인 사용자 정보
        model.addAttribute("posts", postDTOs);  // 게시글 리스트

        return "user/list";  // 게시글 목록 페이지로 이동
    }

    // 게시글 작성 폼
    @GetMapping("/posts/new")
    public String showCreateForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        if (loginMember == null) {
            return "redirect:/login";  // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        model.addAttribute("loginMember", loginMember);  // 로그인 사용자 정보를 모델에 추가
        model.addAttribute("postDTO", new PostDTO());  // 빈 PostDTO 객체 추가
        return "user/create";
    }

    @PostMapping("/posts")
    public String createPost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             @Valid @ModelAttribute("postDTO") PostDTO postDTO, BindingResult result) {
        if (loginMember == null) {
            return "redirect:/login";  // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        if (result.hasErrors()) {
            return "user/create";  // 유효성 검증 실패 시 다시 작성 폼으로 이동
        }

        postDTO.setAuthor(loginMember);  // 작성자를 PostDTO에 설정
        postService.createPost(postDTO);  // PostDTO를 사용하여 저장

        return "redirect:/posts";  // 성공 시 게시글 목록 페이지로 리다이렉트
    }



    @GetMapping("/posts/{id}")
    public String viewPost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                           @PathVariable Long id, Model model) {
        if (loginMember == null) {
            return "redirect:/login";  // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        Post post = postService.getPostById(id);
        if (post == null) {
            model.addAttribute("errorMessage", "해당 게시글을 찾을 수 없습니다.");
            return "error/404";
        }

        List<Comment> comments = commentService.getCommentsByPostId(id);

        // 모델에 댓글 작성 폼을 위한 commentDTO 객체 추가
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentDTO", new CommentDTO());  // 추가 부분

        return "user/view";  // view.html로 이동
    }

    @DeleteMapping("/posts/delete/{id}")
    public String deletePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             @PathVariable Long id) {
        if (loginMember == null) {
            return "redirect:/login";  // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 게시글 삭제 처리 (작성자 검증 제거)
        postService.deletePost(id);
        return "redirect:/posts";  // 삭제 후 게시글 목록 페이지로 리다이렉트
    }

    // 게시글 업데이트
    @PostMapping("/posts/edit/{id}")
    public String updatePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             @PathVariable Long id,
                             @Valid @ModelAttribute("postDTO") PostDTO postDTO,
                             BindingResult result, Model model) {
        if (loginMember == null) {
            return "redirect:/login";  // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        if (result.hasErrors()) {
            return "user/edit";  // 유효성 검증 실패 시 다시 수정 폼으로 이동
        }

        Post post = postService.getPostById(id);
        if (post == null || !post.getAuthor().getId().equals(loginMember.getId())) {
            model.addAttribute("errorMessage", "수정할 권한이 없습니다.");
            return "error/403";  // 권한이 없는 경우
        }

        // 게시글 업데이트 처리
        postService.updatePost(id, postDTO);
        return "redirect:/posts/" + id;  // 수정 후 해당 게시글로 리다이렉트
    }

    // 수정 폼 불러오기
    @GetMapping("/posts/edit/{id}")
    public String showEditForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                               @PathVariable Long id, Model model) {
        if (loginMember == null) {
            return "redirect:/login";  // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        Post post = postService.getPostById(id);
        if (post == null || !post.getAuthor().getId().equals(loginMember.getId())) {
            model.addAttribute("errorMessage", "수정할 권한이 없습니다.");
            return "error/403";  // 권한이 없는 경우
        }

        // 수정할 게시글 정보를 모델에 추가
        model.addAttribute("postDTO", new PostDTO(post));
        return "user/edit";  // 수정 폼 페이지로 이동
    }
    // 제목으로 검색
    @GetMapping("/posts/search")
    public String searchByTitle(@RequestParam("keyword") String keyword, Model model) {
        List<PostDTO> postDTOs = postService.searchByTitle(keyword);
        model.addAttribute("posts", postDTOs);  // PostDTO 리스트를 전달
        return "user/list";  // 템플릿 경로
    }



}
