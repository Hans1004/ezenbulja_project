package ezen.ezenbulja.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "loginid")
    private String loginId;
    private String password;
    private String name;
    private String grade;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Answer> answerList;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Question> questionList;

}
