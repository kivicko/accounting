package com.kivilcimeray.accounting.service;

import com.kivilcimeray.accounting.dto.PlayerDTO;
import com.kivilcimeray.accounting.dto.mapper.PlayerMapper;
import com.kivilcimeray.accounting.model.PaymentTransaction;
import com.kivilcimeray.accounting.model.Player;
import com.kivilcimeray.accounting.repository.PlayerRepository;
import com.kivilcimeray.accounting.util.exception.InsufficientPlayerBalanceException;
import com.kivilcimeray.accounting.util.exception.PlayerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    @Transactional
    public Player findPlayerForWrite(Long id) {
        Optional<Player> optPlayer = playerRepository.findPlayerForWrite(id);

        if (!optPlayer.isPresent()) { //if null
            log.info("findPlayerForWrite error. Player not found. Player ID : {}", id);
            throw new PlayerNotFoundException("player not found for playerID : " + id);
        }

        return optPlayer.get();
    }

    @Transactional
    public Player findPlayerForRead(Long id) {
        Optional<Player> optPlayer = playerRepository.findPlayerForRead(id);

        if (!optPlayer.isPresent()) { //if null
            log.info("findPlayerForRead error. Player not found. Player ID : {}", id);
            throw new PlayerNotFoundException("player not found for playerID : " + id);
        }

        return optPlayer.get();
    }

    @Transactional
    public PlayerDTO getBalanceOfPlayer(Long playerId) {
        Player player = findPlayerForRead(playerId);

        return playerMapper.playerToPlayerDTO(player);
    }

    @Transactional
    public PlayerDTO updatePlayerBalance(PaymentTransaction paymentTransaction) {
        Player player = findPlayerForWrite(paymentTransaction.getPlayer().getId());
        controlPlayerBalance(player, paymentTransaction);

        preparePlayerForUpdate(paymentTransaction, player);

        Player savedPlayer = playerRepository.save(player);

        return playerMapper.playerToPlayerDTO(savedPlayer);
    }

    private void preparePlayerForUpdate(PaymentTransaction paymentTransaction, Player player) {
        player.updateBalance(paymentTransaction.getAmount(), paymentTransaction.getTransactionType());
        player.addTransactionHistory(paymentTransaction);
    }

    private void controlPlayerBalance(Player player, PaymentTransaction paymentTransaction) {
        if (paymentTransaction.isWithdraw() && player.getBalance().compareTo(paymentTransaction.getAmount()) < 0) {
            log.info("Withdrawal Operation Error. Insufficient balance. player : {}, transactionRequest: {}", player, paymentTransaction);
            throw new InsufficientPlayerBalanceException("Player Balance not enough for Withdrawal. Current balance : " + player.getBalance());
        }
    }

    public boolean playerExistById(Long id) {
        return playerRepository.existsById(id);
    }
}