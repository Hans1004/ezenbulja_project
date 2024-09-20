package ezen.ezenbulja.service;


import jakarta.persistence.criteria.*;
import ezen.ezenbulja.domain.dao.Answer;
import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.domain.dao.Question;
import ezen.ezenbulja.repository.AnswerRepository;
import ezen.ezenbulja.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private AnswerRepository answerRepository;

    // 질문 등록
    public void create(Question question) {
        questionRepository.save(question);

    }

    // 모든 질문 조회(내꺼)
//    public Page<Question> getList(int page, String kw) {
//        //sort는 최신글이 위로 올라가게 하는 기능
//        List<Sort.Order> sorts = new ArrayList<>();
//        sorts.add(Sort.Order.desc("createDate"));
//        PageRequest pageable = PageRequest.of(page, 10, Sort.by(sorts));
//        //paga는 조회할 페이지의 번호이고 10은 한페이지에 보여줄 게시물의 개수
//        Specification<Question> spec = search(kw);
//        return this.questionRepository.findAll(spec, pageable);
//    }

    // 모든 질문 조회
    public Page<Question> getList(int page, String kw) {
        //sort는 최신글이 위로 올라가게 하는 기능
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        //paga는 조회할 페이지의 번호이고 10은 한페이지에 보여줄 게시물의 개수
        PageRequest pageable = PageRequest.of(page, 10, Sort.by(sorts));
        //QuestionService에서 search 등록
        Specification<Question> spec = search(kw);
        Page<Question> result = questionRepository.findAll(spec, pageable);
        if (result.hasContent()) {
            return result;
        }
        else {
            return null;
        }
    }


    // 질문 상세페이지
    public Optional<Question> getQuestion(Long id) {

        return questionRepository.findById(id);
    }

    // 질문 삭제 메서드
    public void delete(Question question) {
        // 기존 질문 객체를 데이터베이스에서 찾아옵니다.
        questionRepository.delete(question);
    }

    //검색기능 추가하기
    //JPA가 제공하는 Specfication인터페이스 사용하기, DB검색을 유연하게 할수 있고 복잡한 검색 조건도 처리할수 있음.
    private Specification<Question> search(String kw) {
        return new Specification<Question>() {
            private static final long serialVersionUID = 1L; //직열화 과정에서의 일관성 보장
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                //중복제거
                query.distinct(true);
                Join<Question, Member> m1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Question, Member> m2 = a.join("author", JoinType.LEFT);
                return criteriaBuilder.or(
                        criteriaBuilder.like(q.get("subject"),"%" +kw+"%"),
                        criteriaBuilder.like(q.get("content"),"%" +kw+"%"),
                        criteriaBuilder.like(m1.get("name"),"%" +kw+"%"),
                        criteriaBuilder.like(a.get("content"),"%" +kw+"%"),
                        criteriaBuilder.like(m2.get("name"),"%" +kw+"%")
                );
            }
        };
    }
}
