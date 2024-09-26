package ezen.ezenbulja.repository;

import ezen.ezenbulja.domain.dao.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// PostRepository.java
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    
    // 검색 메소드
    List<Post> findByTitleContaining(String keyword);
}

