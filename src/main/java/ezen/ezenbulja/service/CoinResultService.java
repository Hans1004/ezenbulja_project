package ezen.ezenbulja.service;

import ezen.ezenbulja.domain.dao.CoinResult;
import ezen.ezenbulja.repository.CoinResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class    CoinResultService {

    private final CoinResultRepository coinResultRepository;

    @Autowired
    public CoinResultService(CoinResultRepository coinResultRepository) {
        this.coinResultRepository = coinResultRepository;
    }

    @Transactional
    public void save(CoinResult coinResult) {
        if (coinResult != null) {
            System.out.println("Saving coin result: " + coinResult);
            coinResultRepository.save(coinResult);
        } else {
            System.err.println("CoinResult is null. Cannot save.");
        }
    }


    public List<CoinResult> findAll() {
        List<CoinResult> results = coinResultRepository.findAll();
        System.out.println("Fetched coin results: " + results);
        return results;
    }
    public Page<CoinResult> findAll(Pageable pageable) {
        return coinResultRepository.findAllByOrderByCreatedDateDesc(pageable);
    }
}