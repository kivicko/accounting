package com.kivilcimeray.accounting.dto.mapper;

import com.kivilcimeray.accounting.dto.PlayerDTO;
import com.kivilcimeray.accounting.model.Player;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.kivilcimeray.accounting.util.AccountingUtils.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlayerMapperUnitTest {

    private PlayerMapper mapper = Mappers.getMapper(PlayerMapper.class);

    @Test
    public void shouldMapPlayerToPlayerDTO() {
        BigDecimal sampleAmount = $(100);
        long sampleID = 99L;

        Player samplePlayer = new Player(sampleID, sampleAmount);

        PlayerDTO playerDTO = mapper.playerToPlayerDTO(samplePlayer);

        assertNotNull(playerDTO);
        assertEquals(samplePlayer.getBalance(), playerDTO.getCurrentBalance());
        assertEquals(samplePlayer.getId().longValue(), playerDTO.getPlayerID());
    }
}