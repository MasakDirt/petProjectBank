package com.pet.project.model.dto.card;

import com.pet.project.model.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = java.math.BigDecimal.class)
public interface CardMapper {
    @Mapping(target = "balance", expression = "java(card.getAccount().getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN))")
    CardResponse cardToCardResponse(Card card);
}
