package ezen.ezenbulja.service;

import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원 가입
    public Long join(Member member) {
        validateDuplicateMember(member);
        this.memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 조회 메서드
    private void validateDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository.findByLoginId(member.getLoginId());
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public boolean isLoginIdDuplicate(String loginId) {
        return memberRepository.findByLoginId(loginId).isPresent();
    }

    // 회원 정보 수정 시 같은 아이디 중복 검사
    private void validateDuplicateloginId(Long id, String loginId) {
        Optional<Member> findMember = memberRepository.findByLoginId(loginId);
        if (findMember.isPresent() && !findMember.get().getId().equals(id)) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
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
        validateDuplicateloginId(member.getId(), member.getLoginId()); // 회원 수정 시 중복된 아이디 확인
        memberRepository.save(member);
        return member.getId();
    }

    // 비밀번호 변경
    public boolean changePassword(Long memberId, String newPassword) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setPassword(newPassword);  // 새로운 비밀번호 설정
            memberRepository.save(member);  // 변경된 비밀번호 저장
            return true;
        } else {
            return false;  // 회원이 존재하지 않을 경우 처리
        }
    }

    // 비밀번호가 맞는지 확인하는 메서드
    public boolean isPasswordCorrect(Long memberId, String password) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            // 여기에서 저장된 비밀번호와 사용자가 입력한 비밀번호를 비교
            // 실제로는 해시 알고리즘을 사용할 수 있지만, 여기서는 단순 비교
            return member.getPassword().equals(password);
        }
        return false;
    }
}
