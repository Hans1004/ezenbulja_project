package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dao.CoinResult;
import ezen.ezenbulja.domain.dto.LoginForm;
import ezen.ezenbulja.service.CoinResultService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin
@Controller
public class CoinResultController {

    private final CoinResultService coinResultService;

    @Autowired
    public CoinResultController(CoinResultService coinResultService) {
        this.coinResultService = coinResultService;
    }

    @GetMapping("/coin-data-result")
    public String showResults(@RequestParam(value = "page", defaultValue = "0") int page,HttpServletRequest request, Model model) {
        try {
            // 데이터베이스에서 CoinResult 목록을 페이지 단위로 가져옴
            Page<CoinResult> members = coinResultService.findAll(PageRequest.of(page, 10));
            model.addAttribute("members", members.getContent());
            model.addAttribute("paging", members);

            // 각 CoinResult에 대한 포맷된 가격 설정
            for (CoinResult result : members.getContent()) {
                result.setFormattedPrice(formatPrice(result.getPredictedPrice())); // 포맷된 가격 설정
            }

        } catch (Exception e) {
            model.addAttribute("error", "데이터를 불러오는 중 오류가 발생했습니다.");
            return "error/error_page"; // 에러 페이지로 이동
        }
        return checkLoginAndRedirect(request, model, "page/coin_data_result");
    }



    private String formatPrice(Long price) {
        if (price == null) return "N/A";
        return "₩" + String.format("%,d", price);
    }

    @PostMapping("/predict")
    public ResponseEntity<String> addPrediction(@RequestBody CoinResult coinResult) {
        // 현재 시간으로 createdDate 설정
        coinResult.setCreatedDate(LocalDateTime.now());
        System.out.println("Received prediction: " + coinResult);
        coinResultService.save(coinResult);
        return ResponseEntity.ok("예측 결과가 저장되었습니다.");
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