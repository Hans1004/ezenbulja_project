package ezen.ezenbulja.repository;

import ezen.ezenbulja.domain.dao.LottoResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface LottoRepository extends JpaRepository<LottoResult, Long> {
    // 추가로 필요한 메소드가 있으면 여기에 정의
}
