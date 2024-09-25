package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dto.LoginForm;
import ezen.ezenbulja.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberRepository memberRepository;

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        // 세션이 없거나 로그인하지 않은 경우 index 페이지로 이동
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            return "index"; // 로그아웃 상태에서 index 페이지로 이동
        }

        Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("loginMember", loginMember); // 로그인한 회원 정보 모델에 추가
        return "loginIndex"; // 로그인 상태이면 loginIndex 페이지로 이동
    }

    // 모든 페이지에 대해 로그인 상태 체크
    @GetMapping("/bitcoin")
    public String bitcoin(HttpServletRequest request, Model model) {
        return checkLoginAndRedirect(request, model, "page/coin_data");
    }

    @GetMapping("/estate")
    public String estate(HttpServletRequest request, Model model) {
        return checkLoginAndRedirect(request, model, "page/esti2");
    }

    @GetMapping("/news")
    public String news(HttpServletRequest request, Model model) {
        return checkLoginAndRedirect(request, model, "page/news");
    }

    @GetMapping("/lotto")
    public String lotto(HttpServletRequest request, Model model) {
        return checkLoginAndRedirect(request, model, "page/lotto");
    }
    @GetMapping("/lotto_result")
    public String lotto_result(HttpServletRequest request, Model model) {
        return checkLoginAndRedirect(request, model, "page/lotto_result");
    }

//    @GetMapping("/result_list")
//    public String result_list() {
//        return "page/coin_data_result";
//    }
//    @GetMapping("/result_list")
//    public String result_list(HttpServletRequest request, Model model) {
//        return checkLoginAndRedirect(request, model, "page/coin_data_result");
//    }

    @GetMapping("/member")
    public String member(HttpServletRequest request, Model model) {
        return checkLoginAndRedirect(request, model, "member");
    }
    @GetMapping("/team")
    public String team(HttpServletRequest request, Model model) {
        return checkLoginAndRedirect(request, model, "team_dont");
    }

    // 로그인 상태 체크 후 페이지 이동을 위한 공통 메소드
    private String checkLoginAndRedirect(HttpServletRequest request, Model model, String page) {
        HttpSession session = request.getSession(false);
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
