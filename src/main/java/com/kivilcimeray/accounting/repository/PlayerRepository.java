package com.kivilcimeray.accounting.repository;

import com.kivilcimeray.accounting.model.Player;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "5000")
    })
    @Query(value = "select p from player p where p.id = :id")
    Optional<Player> findPlayerForRead(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "5000")
    })
    @Query(value = "select p from player p where p.id = :id")
    Optional<Player> findPlayerForWrite(Long id);
}