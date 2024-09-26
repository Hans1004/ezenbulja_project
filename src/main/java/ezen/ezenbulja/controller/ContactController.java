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
    public String addContact(@Validated @ModelAttribute("contact") ContactForm contactForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("contact", contactForm); // 에러가 있을 경우 입력값을 다시 모델에 추가
            return "page/contact"; // 에러가 발생하면 contact 페이지로 돌아감
        }

        // 유효성 검사를 통과한 경우, 연락처 저장
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setContent(contactForm.getContent());
        contact.setNews(contactForm.isNews());
        contactService.create(contact);

        return "redirect:/contactList"; // 성공적으로 저장되면 연락처 리스트로 리다이렉트
    }

    @GetMapping("/contactList")
    public String contactList(Model model) {
        model.addAttribute("contacts", contactService.getList()); // 연락처 리스트를 모델에 추가
        return "page/contactList"; // 연락처 리스트 페이지로 이동
    }
}
