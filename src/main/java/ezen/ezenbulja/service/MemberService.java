package ezen.ezenbulja.service;

import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        this.memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository.findByLoginId(member.getLoginId());
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    // 전체 회원 조회
    public List<Member> findMember() {
        return memberRepository.findAll();
    }

    // 회원 1명 조회
    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }

    // 로그인 시 회원 조회
    public Optional<Member> findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }

    // 회원 삭제
    public void delete(Member member) {
        this.memberRepository.delete(member);
    }

    // 회원 수정
    public Long save(Member member) {
//        validateDuplicateloginId(member.getId(), member.getLoginId());
        memberRepository.save(member);
        return member.getId();
    }
}
    // 회원 수정시 같은 아이디 중복 검사(비밀번호나 등급은 수정 가능)--시험 범위 아님.
//    private void validateDuplicateloginId(Long id, String loginId) {
//        Optional<Member> findMember = memberRepository.findByLoginId(loginId);
//        if (findMember.isPresent() && !findMember.get().getId().equals(id)) {
//            throw new IllegalStateException("이미 있다고 ㅠ.ㅠ");
//        }
//    }


