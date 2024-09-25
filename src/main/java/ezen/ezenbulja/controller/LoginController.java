package ezen.ezenbulja.controller;


import ezen.ezenbulja.domain.dao.Member;
import ezen.ezenbulja.domain.dto.LoginForm;
import ezen.ezenbulja.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    // 로그인 폼을 보여주는 메서드
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "user/loginForm"; // 로그인 폼 템플릿
    }

    // 로그인 폼을 처리하는 메서드
    @PostMapping("/login")
    public String loginSubmit(@Valid @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) {
        // 유효성 검사 실패 시 로그인 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            return "user/loginForm";
        }

        // 로그인 서비스 호출
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());



        // 로그인 실패 시 글로벌 에러 추가
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "user/loginForm";
        }

        // request.ghetSession(); 로그인 성공시 세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성, 디폴트가 true
        HttpSession session = request.getSession();
        // 로그인 누가 했는지??
        log.info("loginMember: {}", loginMember.getLoginId());
        // 쿠키에 시간 정보를 주지 않으면 세션쿠키는 브라우저 종료시 모두 종료

        //새션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        // 로그인 성공 시 홈 화면으로 리다이렉트
        return "redirect:/";

    }

    //로그아웃 기능
    //세션 쿠키 이므로 웹 브라우저 종료시
    //서버에서 해당 쿠키릐 종료 날짜를 0으로 지정
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
//        request.getSession().removeAttribute(SessionConst.LOGIN_MEMBER);
        // 세션을 삭제한다.
        HttpSession session = request.getSession(false);
        // true는 세션이 없음녀 만들어 버린다. 일단 가지고 오는데 없으면 null
        if (session != null) {
            session.invalidate(); // 세션을 제거한다.
        }
        return "redirect:/"; // 로그아웃 후 홈 화면으로 리다이렉트
    }


}



