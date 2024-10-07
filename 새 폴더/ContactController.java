package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dao.Contact;
import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.domain.dto.ContactForm;
import ezen.ezenbulja.domain.dto.LoginForm;
import ezen.ezenbulja.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String contact(HttpServletRequest request, Model model) {
        ContactForm contactForm = new ContactForm();
        contactForm.setNews(true); // 기본 값 설정
        model.addAttribute("contact", contactForm);
        return checkLoginAndRedirect(request, model, "page/contact"); // 로그인 여부 체크 후 페이지 이동
    }

    @PostMapping("/submitContact")
    public String addContact(@Validated @ModelAttribute("contact") ContactForm contactForm,
                             BindingResult bindingResult,
                             Model model,
                             HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("contact", contactForm);
            return "page/contact";
        }

        HttpSession session = request.getSession(false); // 세션에서 사용자 정보를 가져옴
        if (session != null) {
            // 세션에서 Member 객체를 가져옴
            Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
            if (loginMember != null) {
                Long userId = loginMember.getId(); // 사용자 ID 가져오기

                Contact contact = new Contact();
                contact.setName(contactForm.getName());
                contact.setEmail(contactForm.getEmail());
                contact.setContent(contactForm.getContent());
                contact.setNews(contactForm.isNews());
                contact.setUserId(userId); // 사용자 ID 설정
                contactService.create(contact);

                return "redirect:/contactList";
            }
        }

        return "redirect:/login"; // 세션이 없거나 로그인하지 않은 경우 로그인 페이지로 리다이렉트
    }



    @GetMapping("/contactList")
    public String contactList(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false); // 세션 가져오기
        if (session != null) {
            Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER); // 로그인된 사용자 정보 가져오기
            if (loginMember != null) {
                Long userId = loginMember.getId(); // 로그인된 사용자의 ID 가져오기
                model.addAttribute("contacts", contactService.getListByUserId(userId)); // 해당 사용자 문의 사항만 조회

                return checkLoginAndRedirect(request, model, "page/contactList");// contactList 페이지로 이동
            }
        }
        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        model.addAttribute("loginForm", new LoginForm()); // 로그인 폼을 모델에 추가
        return "user/loginForm";
    }

    // 로그인 상태 체크 후 페이지 이동을 위한 공통 메소드
    public String checkLoginAndRedirect(HttpServletRequest request, Model model, String page) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
            if (loginMember != null) {
                model.addAttribute("loginMember", loginMember); // 로그인 상태이면 회원 정보 추가
                return page; // 요청한 페이지로 이동
            }
        }
        // 로그인 상태가 아니면 loginForm 페이지로 이동
        model.addAttribute("loginForm", new LoginForm()); // 여기서 loginForm 추가
        return "user/loginForm";
    }
}
