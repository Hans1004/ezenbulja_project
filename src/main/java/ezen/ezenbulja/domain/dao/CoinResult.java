package ezen.ezenbulja.domain.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CoinResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdDate; // 생성일자
    private long predictedPrice; // 예측가격
    private String formattedPrice; // 포맷된 가격 추가

    // 기본 생성자
    public CoinResult() {}

    // 생성자
    public CoinResult(LocalDateTime createdDate, long predictedPrice) {
        this.createdDate = createdDate;
        this.predictedPrice = predictedPrice; // 예측가격
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now(); // 저장되기 전 현재 시간 설정
    }

    @Override
    public String toString() {
        return "CoinResult{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                ", predictedPrice=" + predictedPrice +
                ", formattedPrice='" + formattedPrice + '\'' +
                '}';
    }
}
