package com.kivilcimeray.accounting.service;

import com.kivilcimeray.accounting.dto.TransactionDTO;
import com.kivilcimeray.accounting.dto.mapper.TransactionMapper;
import com.kivilcimeray.accounting.model.PaymentTransaction;
import com.kivilcimeray.accounting.repository.TransactionRepository;
import com.kivilcimeray.accounting.util.exception.PlayerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TransactionMapper transactionMapper;

    public List<TransactionDTO> getAllTransactionRecordsFor(Long playerId) {
        boolean playerExist = playerService.playerExistById(playerId);
        if (!playerExist) { //guard.
            log.info("getAllTransactionRecordsFor method called, player not found. player id : {}", playerId);
            throw new PlayerNotFoundException("Player not found. Player id : " + playerId);
        }

        List<PaymentTransaction> paymentTransactionList = transactionRepository.findAllByPlayerId(playerId);

        return transactionMapper.paymentTransactionsToTransactionDTOList(paymentTransactionList);
    }
}