package ezen.ezenbulja.repository;

import ezen.ezenbulja.domain.dao.CoinResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinResultRepository extends JpaRepository<CoinResult, Long> {
    Page<CoinResult> findAllByOrderByCreatedDateDesc(Pageable pageable);
}
