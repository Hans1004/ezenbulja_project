package ezen.ezenbulja.service;


import ezen.ezenbulja.domain.dao.Post;
import ezen.ezenbulja.domain.dto.PostDTO;
import ezen.ezenbulja.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// PostService.java
@Service
public class PostService {
    private final PostRepository postRepository;

    // 생성자를 통해 PostRepository 주입
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
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
        post.setCreatedAt(LocalDateTime.now());      // 현재 시간을 createdAt에 설정
        return postRepository.save(post);            // 저장 후 저장된 엔티티 반환
    }

    // ID를 기준으로 특정 게시글을 조회하는 메서드
    public Post getPostById(Long id) {
        // ID로 게시글 조회, 없으면 예외 발생
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    // 특정 ID의 게시글을 삭제하는 메서드
    public void deletePost(Long id) {
        // ID로 게시글 삭제
        postRepository.deleteById(id);
    }

    // 수정 기능: 게시글 수정 시 사용 가능 (추가할 수 있음)
}
