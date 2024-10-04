package ezen.ezenbulja.service;

import ezen.ezenbulja.domain.dao.LottoResult;
import ezen.ezenbulja.repository.LottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LottoService {

    @Autowired
    private LottoRepository lottoRepository;

    public LottoResult saveLottoNumbers(int[] numbers) {
        LottoResult result = new LottoResult();
        result.setPredictionDate(LocalDate.now());
        result.setNumber1(numbers[0]);
        result.setNumber2(numbers[1]);
        result.setNumber3(numbers[2]);
        result.setNumber4(numbers[3]);
        result.setNumber5(numbers[4]);
        result.setNumber6(numbers[5]);

        return lottoRepository.save(result);
    }
    // 저장된 모든 로또 번호 가져오기 메소드
    public List<LottoResult> getAllLottoResults() {
        return lottoRepository.findAll();
    }
}