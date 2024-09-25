package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.domain.dto.MemberForm;
import ezen.ezenbulja.service.MemberService;
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
        member.setGrade(("user"));

        memberService.join(member);
        return "redirect:/login"; // 로그인 페이지로 리다이렉트
    }



    // 회원 목록
    @GetMapping(value="/members")
    public String list(@SessionAttribute(name = SessionConst.LOGIN_MEMBER,required=false) Member loginMember, Model model) {
        List<Member> members = memberService.findMember();
        model.addAttribute("members", members);
        model.addAttribute("loginMember", loginMember);
        return "admin/memberList"; // 회원 목록 템플릿
    }


    //회원 삭제
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
    public String editMemberForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER,required=false) Member loginMember,@PathVariable("memberid") Long memberid, Model model) {
        Optional<Member> findMember = memberService.findOne(memberid);
        Member findMemberGet = findMember.get();
        if (findMember.isPresent()) {
            MemberForm memberForm = new MemberForm();
            memberForm.setId(findMemberGet.getId()); // MemberForm Long id 추가
            memberForm.setLoginId(findMemberGet.getLoginId());
            memberForm.setPassword(findMemberGet.getPassword());
            memberForm.setName(findMemberGet.getName());
            memberForm.setGrade(findMemberGet.getGrade());

            model.addAttribute("memberForm", memberForm);
            model.addAttribute("loginMember", loginMember);
            return "admin/editMemberForm";
        }
        model.addAttribute("loginMember", loginMember);
        return "admin/memberList";
    }

    @PostMapping("/members/{memberid}/edit")
    public String editMember(@SessionAttribute(name = SessionConst.LOGIN_MEMBER,required=false) Member loginMember ,@Validated @ModelAttribute("memberForm")MemberForm form, BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", loginMember);
            return "admin/editMemberForm";
        }
            Member member= new Member();
            member.setId(form.getId());
            member.setLoginId(form.getLoginId());
            member.setPassword(form.getPassword());
            member.setName(form.getName());
            member.setGrade(form.getGrade());

            memberService.save(member);
            model.addAttribute("loginMember", loginMember);
            return "redirect:/members";
    }


}




