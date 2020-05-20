package com.kivilcimeray.accounting.dto.mapper;

import com.kivilcimeray.accounting.dto.TransactionDTO;
import com.kivilcimeray.accounting.model.CreditTransaction;
import com.kivilcimeray.accounting.model.PaymentTransaction;
import com.kivilcimeray.accounting.model.Player;
import com.kivilcimeray.accounting.model.WithdrawTransaction;
import com.kivilcimeray.accounting.service.PlayerService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    @Autowired
    private PlayerService playerService;

    @Mapping(source = "player.id", target = "playerID")
    abstract TransactionDTO paymentTransactionToTransactionDTO(PaymentTransaction transaction);

    public abstract List<TransactionDTO> paymentTransactionsToTransactionDTOList(List<PaymentTransaction> transactionList);

    public WithdrawTransaction transactionDTOToWithdrawTransaction(TransactionDTO dto) {
        Player player = playerService.findPlayerForRead(dto.getPlayerID());

        return WithdrawTransaction.builder()
                .amount(dto.getAmount())
                .player(player)
                .transactionCode(dto.getTransactionCode())
                .build();
    }

    public CreditTransaction transactionDTOToCreditTransaction(TransactionDTO dto) {
        Player player = playerService.findPlayerForRead(dto.getPlayerID());

        return CreditTransaction.builder()
                .amount(dto.getAmount())
                .player(player)
                .transactionCode(dto.getTransactionCode())
                .build();
    }
}