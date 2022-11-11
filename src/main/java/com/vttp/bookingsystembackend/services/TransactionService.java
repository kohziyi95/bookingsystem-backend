package com.vttp.bookingsystembackend.services;

import java.util.Optional;
import java.util.logging.Logger;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vttp.bookingsystembackend.models.Transaction;
import com.vttp.bookingsystembackend.repositories.TransactionRepository;

@Service
public class TransactionService {
    private Logger logger = Logger.getLogger(TransactionService.class.getName());

    @Autowired
    private TransactionRepository transactionRepo;

    @Transactional
    public boolean addTransaction(Transaction t) throws Exception {
        Integer updated = transactionRepo.insertTransaction(t);
        if (updated < 1) {
            throw new Exception("Transaction failed.");
        } else {
            return updated >= 1;
        }
    }

    public Float getCredits(Integer userId) {
        Optional<Float> opt = transactionRepo.getCredits(userId);
        if (opt.isEmpty()) {
            return 0f;
        } else {
            return opt.get();
        }
    }


}
