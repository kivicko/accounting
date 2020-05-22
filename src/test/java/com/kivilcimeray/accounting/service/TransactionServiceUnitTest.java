package com.kivilcimeray.accounting.service;

import com.kivilcimeray.accounting.dto.TransactionDTO;
import com.kivilcimeray.accounting.dto.mapper.TransactionMapper;
import com.kivilcimeray.accounting.model.PaymentTransaction;
import com.kivilcimeray.accounting.repository.TransactionRepository;
import com.kivilcimeray.accounting.util.exception.PlayerNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.kivilcimeray.accounting.util.AccountingUtils.$;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceUnitTest {

    @InjectMocks
    private TransactionService service;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private PlayerService playerService;

    @Test
    public void shouldThrowPlayerNotFoundExceptionWhenPlayerIDNotExist() {
        long nonExistingPlayerID = 9999L;

        when(playerService.playerExistById(nonExistingPlayerID)).thenReturn(false);

        try {
            service.getAllTransactionsOfPlayer(nonExistingPlayerID);
            fail();
        } catch (PlayerNotFoundException ex) {
            verify(playerService).playerExistById(nonExistingPlayerID);
            verifyNoInteractions(transactionMapper);
            verifyNoInteractions(transactionRepository);
        }
    }

    @Test
    public void shouldReturnTransactionsWhenPlayerExist() {
        long samplePlayerID = 999L;
        List<PaymentTransaction> unnecessaryList = new ArrayList<>();
        TransactionDTO sampleTransactionDTO = TransactionDTO.builder()
                .playerID(samplePlayerID)
                .amount($(20))
                .transactionCode(UUID.randomUUID())
                .build();

        List<TransactionDTO> sampleTransactionDTOList = Collections.singletonList(sampleTransactionDTO);

        when(playerService.playerExistById(samplePlayerID)).thenReturn(true);
        when(transactionRepository.findAllByPlayerId(samplePlayerID)).thenReturn(unnecessaryList);
        when(transactionMapper.paymentTransactionsToTransactionDTOList(unnecessaryList)).thenReturn(sampleTransactionDTOList);

        List<TransactionDTO> transactionDTOList = service.getAllTransactionsOfPlayer(samplePlayerID);

        assertNotNull(transactionDTOList);
        assertEquals(1, transactionDTOList.size());
        assertEquals(sampleTransactionDTOList, transactionDTOList);
    }
}