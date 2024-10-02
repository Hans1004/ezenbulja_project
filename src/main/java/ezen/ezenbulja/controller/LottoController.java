package ezen.ezenbulja.controller;

import ezen.ezenbulja.domain.dao.LottoResult;
import ezen.ezenbulja.service.LottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LottoController {

    @Autowired
    private LottoService lottoService;

    // 로또 번호 저장 API (JSON 응답)
    @PostMapping("/save-lotto-numbers")
    public String saveLottoNumbers(@RequestBody int[] numbers) {
        lottoService.saveLottoNumbers(numbers);
        return "Lotto numbers saved successfully!";
    }

    // 저장된 로또 번호 리스트 조회 API (JSON 응답)
    @GetMapping("/get-lotto-results")
    public ResponseEntity<List<LottoResult>> getAllLottoResults() {
        List<LottoResult> lottoResults = lottoService.getAllLottoResults();
        return ResponseEntity.ok(lottoResults);
    }
}
