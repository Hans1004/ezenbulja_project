package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dao.LottoResult;
import ezen.ezenbulja.service.LottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LottoWebController {

    @Autowired
    private LottoService lottoService;

    @GetMapping("/lotto_result")
    public String showLottoResults(Model model) {
        List<LottoResult> lottoResults = lottoService.getAllLottoResults();
        System.out.println("로또 결과: " + lottoResults);  // 데이터가 조회되는지 확인
        model.addAttribute("lottoResults", lottoResults);
        return "page/lotto_result";
    }
}
