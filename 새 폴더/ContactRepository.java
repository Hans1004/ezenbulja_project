package ezen.ezenbulja.repository;

import ezen.ezenbulja.domain.dao.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // 사용자 ID별 문의 사항을 조회하는 메소드
    List<Contact> findByUserId(Long userId);
}