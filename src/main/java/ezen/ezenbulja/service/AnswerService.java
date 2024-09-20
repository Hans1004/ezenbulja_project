package ezen.ezenbulja.service;

import ezen.ezenbulja.domain.dao.Answer;
import ezen.ezenbulja.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    public void create(Answer answer) {
        answerRepository.save(answer);
    }

    //답글조회
    public Optional<Answer> getAnswer(Long id) {
        return answerRepository.findById(id);
    }

    // 질문 삭제 메서드
    // 질문 삭제 메서드
    public void delete(Answer answer) {
        // 기존 질문 객체를 데이터베이스에서 찾아옵니다.
        answerRepository.delete(answer);
    }




}
