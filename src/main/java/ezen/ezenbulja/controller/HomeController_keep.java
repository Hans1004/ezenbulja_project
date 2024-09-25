package ezen.ezenbulja.controller;

import ezen.ezenbulja.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
//@Controller
@RequiredArgsConstructor
public class HomeController_keep {
    private final MemberRepository memberRepository;

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return "index"; // 세션이 없으면 index 페이지로 이동
        }

        Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember == null) {
            return "index"; // 세션에 회원 데이터가 없으면 index 페이지로 이동
        }

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

    @GetMapping("/member")
    public String member(HttpServletRequest request, Model model) {
        return checkLoginAndRedirect(request, model, "member");
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
        return "index"; // 로그인 상태가 아니면 index 페이지로 이동
    }
}
