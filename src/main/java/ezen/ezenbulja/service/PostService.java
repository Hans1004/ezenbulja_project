package ezen.ezenbulja.service;


import ezen.ezenbulja.domain.dao.Post;
import ezen.ezenbulja.domain.dto.PostDTO;
import ezen.ezenbulja.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// PostService.java
@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentService commentService;

    // 생성자를 통해 PostRepository 주입
    public PostService(PostRepository postRepository, CommentService commentService) {

        this.postRepository = postRepository;
        this.commentService = commentService;
    }

    // 최신순으로 게시글 목록을 반환하는 메서드
    public List<Post> getAllPosts() {
        // createdAt 필드를 기준으로 내림차순 정렬된 게시글 리스트 반환
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // 새 게시글을 생성하는 메서드
    public Post createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());           // 제목 설정
        post.setContent(postDTO.getContent());       // 내용 설정
        post.setCreatedAt(LocalDateTime.now());      // 현재 시간 설정
        post.setAuthor(postDTO.getAuthor());         // 작성자 설정
        return postRepository.save(post);            // 저장 후 저장된 엔티티 반환
    }

    // ID를 기준으로 특정 게시글을 조회하는 메서드
    public Post getPostById(Long id) {
        // ID로 게시글 조회, 없으면 예외 발생
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    // 게시글 삭제 로직
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 게시글 삭제
        postRepository.deleteById(postId);
    }

    // 게시글 수정
    public void updatePost(Long postId, PostDTO postDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        postRepository.save(post);  // 변경 사항 저장
    }
    public List<PostDTO> searchByTitle(String keyword) {
        List<Post> posts = postRepository.findByTitleContaining(keyword);

        return posts.stream()
                .map(post -> {
                    int commentCount = commentService.getCommentCountByPostId(post.getId());
                    return new PostDTO(post, commentCount);  // 댓글 수를 포함한 PostDTO 생성
                })
                .collect(Collectors.toList());
    }



}
