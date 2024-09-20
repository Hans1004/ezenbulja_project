package ezen.ezenbulja.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Answer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name ="question_id")
    // 많은 답변에 질문이 하나다.
    private Question question;

    @ManyToOne
    @JoinColumn(name ="member_id")
    private Member author;

    private LocalDateTime modifyDate;



}
