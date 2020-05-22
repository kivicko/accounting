package com.kivilcimeray.accounting.controller;

import com.kivilcimeray.accounting.dto.PlayerDTO;
import com.kivilcimeray.accounting.dto.TransactionDTO;
import com.kivilcimeray.accounting.dto.mapper.TransactionMapper;
import com.kivilcimeray.accounting.model.CreditTransaction;
import com.kivilcimeray.accounting.model.Player;
import com.kivilcimeray.accounting.model.WithdrawTransaction;
import com.kivilcimeray.accounting.service.PlayerService;
import com.kivilcimeray.accounting.service.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class AccountControllerUnitTest {

    @InjectMocks
    private AccountController controller;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private PlayerService playerService;

    @Mock
    private TransactionService transactionService;

    @Test
    public void shouldGetBalanceInfoOfPlayerWithGivenID() {
        long samplePlayerID = 999L;
        BigDecimal samplePlayerBalance = $(100);

        PlayerDTO samplePlayerDTO = PlayerDTO.builder()
                .playerID(samplePlayerID)
                .currentBalance(samplePlayerBalance)
                .build();

        when(playerService.getBalanceOfPlayer(samplePlayerID)).thenReturn(samplePlayerDTO);

        PlayerDTO response = controller.balanceOfPlayer(999L);

        assertNotNull(response);
        assertEquals(samplePlayerBalance, response.getCurrentBalance());
        assertEquals(samplePlayerID, response.getPlayerID());
    }

    @Test
    public void shouldGetTransactionHistoryOfPlayerWithGivenID() {
        long samplePLayerID = 100L;
        BigDecimal sampleTransactionAmount = $(123);
        UUID sampleTransactionCode = UUID.randomUUID();

        TransactionDTO sampleTransactionDTO = TransactionDTO.builder()
                .amount(sampleTransactionAmount)
                .playerID(samplePLayerID)
                .transactionCode(sampleTransactionCode)
                .build();

        List<TransactionDTO> sampleTransactionDTOList = Collections.singletonList(sampleTransactionDTO);

        when(transactionService.getAllTransactionsOfPlayer(samplePLayerID)).thenReturn(sampleTransactionDTOList);

        List<TransactionDTO> transactionDTOList = controller.collectHistoryLog(samplePLayerID);

        assertNotNull(transactionDTOList);
        assertEquals(1, transactionDTOList.size());
        assertEquals(sampleTransactionDTOList, transactionDTOList);
    }

    @Test
    public void shouldCreateCreditTransactionWithGivenTransactionDetails() {
        UUID sampleUUID = UUID.randomUUID();
        long samplePlayerID = 123L;
        BigDecimal sampleAmount = $(999);

        TransactionDTO sampleTransactionDTO = TransactionDTO.builder()
                .transactionCode(sampleUUID)
                .playerID(samplePlayerID)
                .amount(sampleAmount)
                .build();

        Player samplePLayer = new Player();

        PlayerDTO playerDTO = PlayerDTO.builder()
                .currentBalance($(0))
                .playerID(samplePlayerID)
                .build();

        CreditTransaction sampleCreditTransaction = CreditTransaction.builder()
                .transactionCode(sampleUUID)
                .amount(sampleAmount)
                .player(samplePLayer)
                .build();

        when(transactionMapper.transactionDTOToCreditTransaction(sampleTransactionDTO)).thenReturn(sampleCreditTransaction);
        when(playerService.updatePlayerBalance(sampleCreditTransaction)).thenReturn(playerDTO);

        PlayerDTO response = controller.credit(sampleTransactionDTO);

        assertNotNull(response);
        assertEquals(playerDTO, response);
        verify(transactionMapper).transactionDTOToCreditTransaction(sampleTransactionDTO);
        verify(playerService).updatePlayerBalance(sampleCreditTransaction);
    }

    @Test
    public void shouldCreateWithdrawalTransactionWithGivenTransactionDetails() {
        UUID sampleUUID = UUID.randomUUID();
        long samplePlayerID = 123L;
        BigDecimal sampleAmount = $(999);

        TransactionDTO sampleTransactionDTO = TransactionDTO.builder()
                .transactionCode(sampleUUID)
                .playerID(samplePlayerID)
                .amount(sampleAmount)
                .build();

        Player samplePLayer = new Player();

        PlayerDTO playerDTO = PlayerDTO.builder()
                .currentBalance($(0))
                .playerID(samplePlayerID)
                .build();

        WithdrawTransaction sampleWithdrawTransaction = WithdrawTransaction.builder()
                .transactionCode(sampleUUID)
                .amount(sampleAmount)
                .player(samplePLayer)
                .build();

        when(transactionMapper.transactionDTOToWithdrawTransaction(sampleTransactionDTO)).thenReturn(sampleWithdrawTransaction);
        when(playerService.updatePlayerBalance(sampleWithdrawTransaction)).thenReturn(playerDTO);

        PlayerDTO response = controller.withdraw(sampleTransactionDTO);

        assertNotNull(response);
        assertEquals(playerDTO, response);
        verify(transactionMapper).transactionDTOToWithdrawTransaction(sampleTransactionDTO);
        verify(playerService).updatePlayerBalance(sampleWithdrawTransaction);
    }
}