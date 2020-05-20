package com.kivilcimeray.accounting.repository;

import com.kivilcimeray.accounting.model.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static com.kivilcimeray.accounting.util.AccountingUtils.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository repository;

    @Test
    public void shouldGetPlayerWithReadLock() {
        BigDecimal sampleBalance = $(100);
        long samplePlayerID = 123L;

        Player samplePlayer = new Player(samplePlayerID, sampleBalance);
        entityManager.persist(samplePlayer);
        entityManager.flush();

        Optional<Player> playerForRead = repository.findPlayerForRead(samplePlayerID);

        assertTrue(playerForRead.isPresent());
        Player player = playerForRead.get();
        assertEquals(samplePlayer, player);
    }

    @Test
    public void shouldGetPlayerWithWriteLock() {
        BigDecimal sampleBalance = $(100);
        long samplePlayerID = 123L;

        Player samplePlayer = new Player(samplePlayerID, sampleBalance);
        entityManager.persist(samplePlayer);
        entityManager.flush();

        Optional<Player> playerForWrite = repository.findPlayerForWrite(samplePlayerID);

        assertTrue(playerForWrite.isPresent());
        Player player = playerForWrite.get();
        assertEquals(samplePlayer, player);
    }
}