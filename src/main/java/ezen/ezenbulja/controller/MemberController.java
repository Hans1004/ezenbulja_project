package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.domain.dto.MemberForm;
import ezen.ezenbulja.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 가입 폼
    @GetMapping("/add")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "user/addMemberForm"; // 회원 가입 폼 템플릿
    }

    @PostMapping("/add")
    public String createMember(@Validated @ModelAttribute("memberForm") MemberForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/addMemberForm";
        }
        Member member = new Member();
        member.setLoginId(form.getLoginId());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        member.setGrade("user");

        memberService.join(member);
        return "redirect:/login"; // 로그인 페이지로 리다이렉트
    }

    // 회원 목록
    @GetMapping(value = "/members")
    public String list(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        List<Member> members = memberService.findMember();
        model.addAttribute("members", members);
        model.addAttribute("loginMember", loginMember);
        return "admin/memberList"; // 회원 목록 템플릿
    }

    // 회원 삭제
    @GetMapping(value = "/members/{memberid}/delete")
    public String delete(@PathVariable("memberid") Long memberid) {
        Optional<Member> findMember = memberService.findOne(memberid);

        if (findMember.isPresent()) {
            Member deleteMember = findMember.get();
            memberService.delete(deleteMember);
        }
        return "redirect:/members"; // 삭제 후 리다이렉트
    }

    // 회원 수정 폼 표시
    @GetMapping("/members/{memberid}/edit")
    public String editMemberForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, @PathVariable("memberid") Long memberid, Model model) {
        model.addAttribute("loginMember", loginMember);

        // 로그인 검증 추가
        if (loginMember == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 회원 찾기
        Optional<Member> findMember = memberService.findOne(memberid);
        if (!findMember.isPresent()) {
            model.addAttribute("errorMessage", "해당 회원을 찾을 수 없습니다.");
            return "admin/memberList";
        }

        Member findMemberGet = findMember.get();
        MemberForm memberForm = new MemberForm();
        memberForm.setId(findMemberGet.getId());
        memberForm.setLoginId(findMemberGet.getLoginId());
        memberForm.setName(findMemberGet.getName());
        memberForm.setGrade(findMemberGet.getGrade());

        // 비밀번호는 폼에서 입력받도록 제외
        // memberForm.setPassword(findMemberGet.getPassword());

        model.addAttribute("memberForm", memberForm);
        return "admin/editMemberForm";
    }


    @PostMapping("/members/{memberid}/edit")
    public String editMember(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, @Validated @ModelAttribute("memberForm") MemberForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember);
            return "admin/editMemberForm";
        }

        Member member = new Member();
        member.setId(form.getId());
        member.setLoginId(form.getLoginId());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        member.setGrade(form.getGrade());

        memberService.save(member);
        model.addAttribute("loginMember", loginMember);
        return "redirect:/members";
    }

    // 비밀번호 변경 폼
    @GetMapping("/change-password")
    public String changePasswordForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        if (loginMember == null) {
            return "redirect:/login";  // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 기존 로그인 정보를 활용하여 MemberForm에 데이터 채움
        MemberForm memberForm = new MemberForm();
        memberForm.setLoginId(loginMember.getLoginId());  // 로그인한 사용자의 아이디 설정
        memberForm.setName(loginMember.getName());  // 로그인한 사용자의 이름 설정

        model.addAttribute("memberForm", memberForm);  // form 객체 추가
        model.addAttribute("loginMember", loginMember);  // 로그인한 사용자 정보 추가

        return "user/change-password";  // 비밀번호 변경 페이지로 이동
    }

    @PostMapping("/change-password")
    public String changePassword(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                                 @Validated @ModelAttribute("memberForm") MemberForm form,
                                 BindingResult bindingResult,
                                 Model model, HttpServletRequest request) {

//        System.out.println("비밀번호 변경 요청 - loginId: " + form.getLoginId() + ", name: " + form.getName());

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (loginMember == null) {
            System.out.println("로그인 정보가 없습니다.");  // 로그 출력
            return "redirect:/login";
        }

//        System.out.println("로그인 상태 유지 중. 사용자 ID: " + loginMember.getLoginId());  // 디버깅 로그 추가

        // 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            System.out.println("유효성 검증 실패: " + bindingResult.getAllErrors());  // 오류 내용을 로그로 출력
            return "user/change-password";
        }

        // 새 비밀번호와 확인 비밀번호가 일치하지 않을 경우
        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            System.out.println("새 비밀번호와 확인 비밀번호가 일치하지 않음");
            bindingResult.rejectValue("confirmNewPassword", "error.confirmNewPassword", "새 비밀번호가 일치하지 않습니다.");
            return "user/change-password";
        }

        // (선택 사항) 현재 비밀번호가 맞는지 확인하는 로직 추가
        if (!memberService.isPasswordCorrect(loginMember.getId(), form.getPassword())) {
            System.out.println("현재 비밀번호가 일치하지 않음");
            bindingResult.rejectValue("password", "error.password", "현재 비밀번호가 맞지 않습니다.");
            return "user/change-password";
        }

        // 비밀번호 변경 로직
        boolean isPasswordChanged = memberService.changePassword(loginMember.getId(), form.getNewPassword());

        // 비밀번호 변경 실패 시
        if (!isPasswordChanged) {
            System.out.println("비밀번호 변경 실패");
            bindingResult.rejectValue("newPassword", "error.newPassword", "비밀번호 변경에 실패했습니다.");
            return "user/change-password";
        }

//        System.out.println("비밀번호 변경 성공");

        // 세션에 로그인 정보를 갱신 (비밀번호 변경 후에도 세션에 업데이트된 정보 유지)
        loginMember.setPassword(form.getNewPassword());
        request.getSession().setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        // 세션에 저장된 비밀번호가 올바르게 변경되었는지 확인 (디버깅용)
//        System.out.println("세션에 저장된 비밀번호: " + loginMember.getPassword());

        return "redirect:/";  // 비밀번호 변경 성공 시 홈으로 리다이렉트
    }


}
