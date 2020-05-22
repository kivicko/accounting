package com.kivilcimeray.accounting.controller;

import com.kivilcimeray.accounting.dto.PlayerDTO;
import com.kivilcimeray.accounting.dto.TransactionDTO;
import com.kivilcimeray.accounting.dto.mapper.TransactionMapper;
import com.kivilcimeray.accounting.model.CreditTransaction;
import com.kivilcimeray.accounting.model.WithdrawTransaction;
import com.kivilcimeray.accounting.service.PlayerService;
import com.kivilcimeray.accounting.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AccountController {

    private final TransactionMapper transactionMapper;
    private final PlayerService playerService;
    private final TransactionService transactionService;

    public AccountController(TransactionMapper transactionMapper, PlayerService playerService, TransactionService transactionService) {
        this.transactionMapper = transactionMapper;
        this.playerService = playerService;
        this.transactionService = transactionService;
    }

    @GetMapping("/balance/{id}")
    public PlayerDTO balanceOfPlayer(@PathVariable Long id) {
        return playerService.getBalanceOfPlayer(id);
    }

    @GetMapping("/log/{id}")
    public List<TransactionDTO> collectHistoryLog(@PathVariable Long id) {
        return transactionService.getAllTransactionsOfPlayer(id);
    }

    @PostMapping(value = "/credit")
    public PlayerDTO credit(@Valid @RequestBody TransactionDTO transactionDTO) {
        CreditTransaction creditTransaction = transactionMapper.transactionDTOToCreditTransaction(transactionDTO);

        return playerService.updatePlayerBalance(creditTransaction);
    }

    @PostMapping("/withdraw")
    public PlayerDTO withdraw(@Valid @RequestBody TransactionDTO transactionDTO) {
        WithdrawTransaction withdrawTransaction = transactionMapper.transactionDTOToWithdrawTransaction(transactionDTO);

        return playerService.updatePlayerBalance(withdrawTransaction);
    }
}