package ezen.ezenbulja.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CoinResultForm {

    private Long id;
    private LocalDateTime createdDate; // 생성일자
    private double predictedPrice; // 예측가격

    // 기본 생성자
    public CoinResultForm() {}

    // 필요 시 생성자 추가 가능
    public CoinResultForm(Long id, LocalDateTime createdDate, double predictedPrice) {
        this.id = id;
        this.createdDate = createdDate;
        this.predictedPrice = predictedPrice;
    }
}
