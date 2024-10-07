package ezen.ezenbulja.service;

import ezen.ezenbulja.domain.dao.Contact;
import ezen.ezenbulja.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    // 모든 문의 사항 리스트 조회
    public List<Contact> getList() {
        return contactRepository.findAll();
    }

    // 사용자 ID별로 문의 사항을 조회하는 메소드
    public List<Contact> getListByUserId(Long userId) {
        return contactRepository.findByUserId(userId);
    }

    // 문의 사항을 생성 및 저장
    public Contact create(Contact contact) {
        return contactRepository.save(contact);
    }
}
