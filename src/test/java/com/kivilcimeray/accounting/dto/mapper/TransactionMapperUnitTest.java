package com.kivilcimeray.accounting.dto.mapper;

import com.kivilcimeray.accounting.dto.TransactionDTO;
import com.kivilcimeray.accounting.model.*;
import com.kivilcimeray.accounting.service.PlayerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.kivilcimeray.accounting.util.AccountingUtils.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionMapperUnitTest {

    @InjectMocks
    private TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    @Mock
    private PlayerService playerService;

    @Test
    public void shouldMapPaymentTransactionToTransactionDTO() {
        Player samplePlayer = new Player(999L, $(9999));

        UUID randomUUID = UUID.randomUUID();

        PaymentTransaction paymentTransaction = CreditTransaction.builder()
                .player(samplePlayer)
                .amount($(100))
                .transactionCode(randomUUID)
                .build();

        TransactionDTO transactionDTO = mapper.paymentTransactionToTransactionDTO(paymentTransaction);

        assertNotNull(transactionDTO);
        assertEquals(paymentTransaction.getAmount(), transactionDTO.getAmount());
        assertEquals(paymentTransaction.getPlayer().getId(), transactionDTO.getPlayerID());
        assertEquals(paymentTransaction.getTransactionCode(), transactionDTO.getTransactionCode());
    }

    @Test
    public void shouldMapPaymentTransactionListToPaymentTransactionDTOList() {
        Player samplePlayer = new Player(999L, $(9999));

        UUID randomUUID = UUID.randomUUID();

        PaymentTransaction paymentTransaction = CreditTransaction.builder()
                .player(samplePlayer)
                .amount($(100))
                .transactionCode(randomUUID)
                .build();

        List<TransactionDTO> transactionDTOList = mapper.paymentTransactionsToTransactionDTOList(Collections.singletonList(paymentTransaction));

        assertNotNull(transactionDTOList);
        assertEquals(1, transactionDTOList.size());
        TransactionDTO transactionDTO = transactionDTOList.get(0);

        assertNotNull(transactionDTO);
        assertEquals(paymentTransaction.getAmount(), transactionDTO.getAmount());
        assertEquals(paymentTransaction.getPlayer().getId(), transactionDTO.getPlayerID());
        assertEquals(paymentTransaction.getTransactionCode(), transactionDTO.getTransactionCode());
    }

    @Test
    public void shouldMapTransactionDTOToCreditTransaction() {
        long samplePlayerID = 99L;
        BigDecimal sampleAmount = $(1000);
        Player samplePlayer = new Player(samplePlayerID, sampleAmount);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionCode(UUID.randomUUID())
                .amount($(50))
                .playerID(samplePlayerID)
                .build();

        when(playerService.findPlayerForRead(samplePlayerID)).thenReturn(samplePlayer);

        CreditTransaction creditTransaction = mapper.transactionDTOToCreditTransaction(transactionDTO);

        assertNotNull(creditTransaction);
        assertEquals(TransactionType.CREDIT, creditTransaction.getTransactionType());
        assertEquals(samplePlayer, creditTransaction.getPlayer());
        assertEquals(transactionDTO.getAmount(), creditTransaction.getAmount());
        assertEquals(transactionDTO.getTransactionCode(), creditTransaction.getTransactionCode());
        verify(playerService).findPlayerForRead(samplePlayerID);
    }

    @Test
    public void shouldMapTransactionDTOToWithdrawTransaction() {
        long samplePlayerID = 99L;
        BigDecimal sampleAmount = $(1000);
        Player samplePlayer = new Player(samplePlayerID, sampleAmount);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionCode(UUID.randomUUID())
                .amount($(50))
                .playerID(samplePlayerID)
                .build();

        when(playerService.findPlayerForRead(samplePlayerID)).thenReturn(samplePlayer);

        WithdrawTransaction withdrawTransaction = mapper.transactionDTOToWithdrawTransaction(transactionDTO);

        assertNotNull(withdrawTransaction);
        assertEquals(TransactionType.WITHDRAW, withdrawTransaction.getTransactionType());
        assertEquals(samplePlayer, withdrawTransaction.getPlayer());
        assertEquals(transactionDTO.getAmount(), withdrawTransaction.getAmount());
        assertEquals(transactionDTO.getTransactionCode(), withdrawTransaction.getTransactionCode());
        verify(playerService).findPlayerForRead(samplePlayerID);
    }

}