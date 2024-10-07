package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dao.Contact;
import ezen.ezenbulja.domain.dto.ContactForm;
import ezen.ezenbulja.service.ContactService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        ContactForm contactForm = new ContactForm();
        contactForm.setNews(true);
        model.addAttribute("contact", contactForm);
        return "page/contact"; // 문의 페이지로 이동
    }

    @PostMapping("/submitContact")
    public String addContact(@Validated @ModelAttribute("contact") ContactForm contactForm,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("contact", contactForm);
            return "page/contact"; // 에러가 발생하면 contact 페이지로 돌아감
        }

        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER); // 세션에서 사용자 ID 가져오기

        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setContent(contactForm.getContent());
        contact.setNews(contactForm.isNews());
        contact.setUserId(userId); // 사용자 ID 설정
        contactService.create(contact);

        return "redirect:/contactList"; // 성공적으로 저장되면 연락처 리스트로 리다이렉트
    }

    @GetMapping("/contactList")
    public String contactList(Model model) {
        model.addAttribute("contacts", contactService.getList()); // 모든 문의 사항 리스트를 조회
        return "page/contactList"; // 연락처 리스트 페이지로 이동
    }
}
