package ezen.ezenbulja.service;

import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    public Member login(String loginId, String password) {
        Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);
        if (findMemberOptional.isPresent()) {
            Member member = findMemberOptional.get(); // 값을 꺼낸다.
            if (member.getPassword().equals(password)) { // 값을 검증한다.
                return member;
            }
            else {
                return null;
            }
        }
        return null;
    }
}


