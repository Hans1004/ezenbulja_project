package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dto.PostDTO;
import ezen.ezenbulja.service.PostService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// PostController.java
@Controller
public class PostController {
    private final PostService postService;

    // 생성자 주입을 통해 PostService 의존성을 주입
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 목록 페이지 (최신순으로 정렬된 게시글 리스트를 모델에 추가)
    @GetMapping("/posts")
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());  // 최신순 게시글을 모델에 추가
        return "user/list";  // 타임리프 템플릿 list.html로 이동
    }

    // 게시글 작성 폼을 보여주는 페이지
    @GetMapping("/posts/new")
    public String showCreateForm(Model model) {
        model.addAttribute("postDTO", new PostDTO());  // 비어 있는 PostDTO 객체를 모델에 추가
        return "user/create";  // 타임리프 템플릿 create.html로 이동
    }

    // 새 게시글을 생성하는 처리
    @PostMapping("/posts")
    public String createPost(@Valid @ModelAttribute("postDTO") PostDTO postDTO, BindingResult result) {
        if (result.hasErrors()) {  // 유효성 검증에서 에러가 있으면 다시 폼으로 이동
            return "user/create";
        }
        postService.createPost(postDTO);  // 게시글 생성
        return "redirect:/posts";  // 게시글 목록 페이지로 리다이렉트
    }

    // 개별 게시글 보기 페이지
    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));  // 게시글 데이터를 모델에 추가
        return "user/view";  // 타임리프 템플릿 view.html로 이동
    }

    // 게시글 삭제 처리
    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);  // 해당 ID의 게시글 삭제
        return "redirect:/posts";  // 삭제 후 게시글 목록 페이지로 리다이렉트
    }
}
