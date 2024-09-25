package ezen.ezenbulja.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 사용자 권한 확인
        Object grade = request.getSession().getAttribute("grade");
        log.info("로그인 회원 권한 : {}", grade);

        // 관리자(admin) 접근 제한
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/admin") && !"admin".equals(grade)) {
            response.sendRedirect("/access-denied");
            return false;
        }

        // 사용자(user) 접근 제한
        if (requestURI.startsWith("/user") && !"user".equals(grade) && !"admin".equals(grade)) {
            response.sendRedirect("/access-denied");
            return false;
        }

        return true;
    }
}
