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

    /**
     * 새로운 연락처를 생성하고 저장합니다.
     * @param contact 저장할 연락처 객체
     * @return 저장된 연락처의 ID
     */
    public Long create(Contact contact) {
        // 필요에 따라 추가적인 유효성 검사나 로직을 여기에 구현
        contactRepository.save(contact);
        return contact.getId();
    }

    /**
     * 모든 연락처의 목록을 반환합니다.
     * @return 연락처 목록
     */
    public List<Contact> getList() {
        return contactRepository.findAll();
    }
}
