package ezen.ezenbulja.controller;


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
    // required = false : 비회원도 로그인 화면은 봐야하니까
    // login 후 화면으로 가기 위한 로직
    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        //세션이 없으면 home
        HttpSession session = request.getSession(false);

        if (session == null) {
            return "index";
        }
        //로그인, 저장소에 가서 아이디를 찾아 꺼냄
        Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
        // 세션에 회원데이터가 없으면 home
        if (loginMember == null) {
            return "index";
        }
        // 로그인 시점에 세션에 보관한 회원 객체를 찾는다.
//        Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
//        //세션에 회원데이터가 없으면 home
//        if(loginMember == null){
//            return "home";
//        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("loginMember", loginMember);
        return "loginIndex";
    }
    @GetMapping("/bitcoin")
    public String bitcoin() {
        return "page/coin_data";
    }

    @GetMapping("/estate")
    public String estate() {
        return "page/esti2";
    }

    @GetMapping("/news")
    public String news() {
        return "page/news";
    }

    @GetMapping("/lotto")
    public String lotto() {
        return "page/lotto";
    }

    @GetMapping("/lotto_result")
    public String lotto_result() { return "page/lotto_result"; }

    @GetMapping("/member")
    public String member() {
        return "member";
    }
}