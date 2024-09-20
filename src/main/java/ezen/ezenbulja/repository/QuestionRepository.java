package ezen.ezenbulja.repository;

import ezen.ezenbulja.domain.dao.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 제목으로 찾기

    // 제목과 내용으로 찾기
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
    //findAll 메서드

}

