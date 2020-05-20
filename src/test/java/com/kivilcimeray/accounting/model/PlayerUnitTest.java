package com.kivilcimeray.accounting.model;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.kivilcimeray.accounting.util.AccountingUtils.$;
import static org.junit.Assert.*;

public class PlayerUnitTest {

    private BigDecimal sampleBalance;
    private long sampleID;
    private Player player;

    @Before
    public void before() {
        sampleID = 99L;
        sampleBalance = $(100);
        player = new Player(sampleID, sampleBalance);
    }

    @Test
    public void shouldSubtractBalanceWhenTransactionTypeIsWithdraw() {
        player.updateBalance($(50), TransactionType.WITHDRAW);

        assertEquals($(50), player.getBalance());
    }

    @Test
    public void shouldAddBalanceWhenTransactionTypeIsCredit() {
        player.updateBalance($(50), TransactionType.CREDIT);

        assertEquals($(150), player.getBalance());
    }

    @Test
    public void shouldCreateNewTransactionHistoryListWhenNull() {
        WithdrawTransaction sampleTransaction = new WithdrawTransaction();

        assertNull(player.getTransactionHistory());

        player.addTransactionHistory(sampleTransaction);

        assertNotNull(player.getTransactionHistory());
        assertEquals(1, player.getTransactionHistory().size());
        assertEquals(sampleTransaction, player.getTransactionHistory().get(0));
    }
}