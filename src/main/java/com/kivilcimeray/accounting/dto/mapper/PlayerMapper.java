package com.kivilcimeray.accounting.dto.mapper;

import com.kivilcimeray.accounting.dto.PlayerDTO;
import com.kivilcimeray.accounting.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @Mappings({
            @Mapping(source = "id", target = "playerID"),
            @Mapping(source = "balance", target = "currentBalance")
    })
    PlayerDTO playerToPlayerDTO(Player player);
}
