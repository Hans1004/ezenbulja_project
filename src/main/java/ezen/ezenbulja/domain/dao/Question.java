package ezen.ezenbulja.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "question_id")
    private Long id;
    @Column(length = 200)
    private String subject;

    // columnDefinition = "TEXT" : 텏그트 열 데이터로 넣을 수 있음을 의미하고 글자 수를 제한 할수 없는 경우에 사용한다.
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    // 하나의 질문에 답변이 여러개일수있다.
    // 주인 : (mappedBy = "question")
    // cascade = CascadeType.REMOVE : 질문을 삭제하면 답변들도 삭제된다.
    private List<Answer> answerList;

    @ManyToOne
    @JoinColumn(name = "member_id") //join할 주키
    private Member author; // 로그인 사용자

    private LocalDateTime modifyDate;

}
