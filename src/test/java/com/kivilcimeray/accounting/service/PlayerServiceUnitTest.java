package com.kivilcimeray.accounting.service;

import com.kivilcimeray.accounting.dto.PlayerDTO;
import com.kivilcimeray.accounting.dto.mapper.PlayerMapper;
import com.kivilcimeray.accounting.model.CreditTransaction;
import com.kivilcimeray.accounting.model.PaymentTransaction;
import com.kivilcimeray.accounting.model.Player;
import com.kivilcimeray.accounting.model.WithdrawTransaction;
import com.kivilcimeray.accounting.repository.PlayerRepository;
import com.kivilcimeray.accounting.util.exception.InsufficientPlayerBalanceException;
import com.kivilcimeray.accounting.util.exception.PlayerNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.kivilcimeray.accounting.util.AccountingUtils.$;
import static org.hamcrest.Matchers.any;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerServiceUnitTest {

    @InjectMocks
    private PlayerService service;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerMapper playerMapper;

    @Test
    public void shouldThrowExceptionOnFindPlayerForWriteWhenPlayerNotExist() {
        Long samplePlayerId = 123L;

        when(playerRepository.findPlayerForWrite(samplePlayerId)).thenReturn(Optional.empty());

        try {
            service.findPlayerForWrite(samplePlayerId);
            fail();
        } catch (PlayerNotFoundException ex) {
            assertEquals("player not found for playerID : 123", ex.getMessage());
            verify(playerRepository).findPlayerForWrite(samplePlayerId);
        }
    }

    @Test
    public void shouldReturnPlayerWhenPlayerExist() {
        Long samplePlayerId = 123L;
        BigDecimal playerBalance = $(0);
        Player samplePlayer = new Player(samplePlayerId, playerBalance);

        when(playerRepository.findPlayerForWrite(samplePlayerId)).thenReturn(Optional.of(samplePlayer));

        Player player = service.findPlayerForWrite(samplePlayerId);
        assertNotNull(player);
        assertEquals(samplePlayer, player);
        verify(playerRepository).findPlayerForWrite(samplePlayerId);
    }

    @Test
    public void shouldThrowExceptionOnFindPlayerForReadWhenPlayerNotExist() {
        Long samplePlayerId = 123L;

        when(playerRepository.findPlayerForRead(samplePlayerId)).thenReturn(Optional.empty());

        try {
            service.findPlayerForRead(samplePlayerId);
            fail();
        } catch (PlayerNotFoundException ex) {
            assertEquals("player not found for playerID : 123", ex.getMessage());
            verify(playerRepository).findPlayerForRead(samplePlayerId);
        }
    }

    @Test
    public void shouldReturnPlayerWhenPlayerExistOnRead() {
        Long samplePlayerId = 123L;
        BigDecimal playerBalance = $(0);
        Player samplePlayer = new Player(samplePlayerId, playerBalance);

        when(playerRepository.findPlayerForRead(samplePlayerId)).thenReturn(Optional.of(samplePlayer));

        Player player = service.findPlayerForRead(samplePlayerId);
        assertNotNull(player);
        assertEquals(samplePlayer, player);
        verify(playerRepository).findPlayerForRead(samplePlayerId);
    }

    @Test
    public void shouldReturnBalanceOfPlayer() {
        Long samplePlayerID = 999L;
        BigDecimal samplePlayerBalance = $(100);
        Player samplePlayer = new Player(samplePlayerID, samplePlayerBalance);
        PlayerDTO samplePlayerDTO = PlayerDTO.builder()
                .playerID(samplePlayerID)
                .currentBalance(samplePlayerBalance)
                .build();

        when(playerRepository.findPlayerForRead(samplePlayerID)).thenReturn(Optional.of(samplePlayer));
        when(playerMapper.playerToPlayerDTO(samplePlayer)).thenReturn(samplePlayerDTO);

        PlayerDTO playerDTO = service.getBalanceOfPlayer(samplePlayerID);

        assertNotNull(playerDTO);
        assertEquals(samplePlayerDTO, playerDTO);
        verify(playerMapper).playerToPlayerDTO(samplePlayer);
        verify(playerRepository).findPlayerForRead(samplePlayerID);
    }

    @Test
    public void shouldReducePlayerBalanceWhenUpdateBalanceCalledWithWithdraw() {
        UUID sampleUUID = UUID.randomUUID();
        BigDecimal sampleAmount = $(100);
        Player samplePlayer = new Player(1L, $(100));

        PlayerDTO samplePlayerDTO = PlayerDTO.builder()
                .currentBalance($(0))
                .playerID(samplePlayer.getId())
                .build();

        PaymentTransaction samplePT = WithdrawTransaction.builder()
                .transactionCode(sampleUUID)
                .amount(sampleAmount)
                .player(samplePlayer)
                .build();

        when(playerRepository.findPlayerForWrite(samplePlayer.getId())).thenReturn(Optional.of(samplePlayer));
        when(playerRepository.save(ArgumentMatchers.any(Player.class))).thenReturn(samplePlayer);
        when(playerMapper.playerToPlayerDTO(samplePlayer)).thenReturn(samplePlayerDTO);


        PlayerDTO playerDTO = service.updatePlayerBalance(samplePT);

        assertNotNull(playerDTO);
        assertEquals($(0), playerDTO.getCurrentBalance());
        assertEquals(samplePlayer.getId().longValue(), playerDTO.getPlayerID());
        verify(playerRepository).findPlayerForWrite(samplePlayer.getId());
        verify(playerRepository).save(ArgumentMatchers.any(Player.class));
        verify(playerMapper).playerToPlayerDTO(samplePlayer);
    }

    @Test
    public void shouldReducePlayerBalanceWhenUpdateBalanceCalledWithCredit() {
        UUID sampleUUID = UUID.randomUUID();
        BigDecimal sampleAmount = $(100);
        Player samplePlayer = new Player(1L, $(100));

        PlayerDTO samplePlayerDTO = PlayerDTO.builder()
                .currentBalance($(150))
                .playerID(samplePlayer.getId())
                .build();

        PaymentTransaction samplePT = CreditTransaction.builder()
                .transactionCode(sampleUUID)
                .amount(sampleAmount)
                .player(samplePlayer)
                .build();

        when(playerRepository.findPlayerForWrite(samplePlayer.getId())).thenReturn(Optional.of(samplePlayer));
        when(playerRepository.save(ArgumentMatchers.any(Player.class))).thenReturn(samplePlayer);
        when(playerMapper.playerToPlayerDTO(samplePlayer)).thenReturn(samplePlayerDTO);


        PlayerDTO playerDTO = service.updatePlayerBalance(samplePT);

        assertNotNull(playerDTO);
        assertEquals($(150), playerDTO.getCurrentBalance());
        assertEquals(samplePlayer.getId().longValue(), playerDTO.getPlayerID());
        verify(playerRepository).findPlayerForWrite(samplePlayer.getId());
        verify(playerRepository).save(ArgumentMatchers.any(Player.class));
        verify(playerMapper).playerToPlayerDTO(samplePlayer);
    }


    @Test
    public void shouldThrowInsufficientBalanceExceptionWhenTransactionIsWithdrawAndBalanceIsNotEnough() {
        UUID sampleUUID = UUID.randomUUID();
        BigDecimal sampleAmount = $(100);
        Player samplePlayer = new Player(1L, $(10));

        PaymentTransaction samplePT = WithdrawTransaction.builder()
                .transactionCode(sampleUUID)
                .amount(sampleAmount)
                .player(samplePlayer)
                .build();

        when(playerRepository.findPlayerForWrite(samplePlayer.getId())).thenReturn(Optional.of(samplePlayer));

        try {
            service.updatePlayerBalance(samplePT);
            fail();
        } catch (InsufficientPlayerBalanceException ex) {
            verify(playerRepository).findPlayerForWrite(samplePlayer.getId());
            verify(playerRepository, times(0)).save(ArgumentMatchers.any(Player.class));
            verify(playerMapper, times(0)).playerToPlayerDTO(samplePlayer);
        }
    }

    @Test
    public void shouldReturnTrueWhenGivenPlayerIDExist() {
        Long samplePlayerID = 999L;

        when(playerRepository.existsById(samplePlayerID)).thenReturn(true);

        boolean b = service.playerExistById(samplePlayerID);

        assertTrue(b);
        verify(playerRepository).existsById(samplePlayerID);
    }

    @Test
    public void shouldReturnFalseWhenGivenPlayerIDNotExist() {
        Long samplePlayerID = 999L;

        when(playerRepository.existsById(samplePlayerID)).thenReturn(false);

        boolean b = service.playerExistById(samplePlayerID);

        assertFalse(b);
        verify(playerRepository).existsById(samplePlayerID);
    }
}