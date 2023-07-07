package com.pet.project.model.dto.card;

import com.pet.project.model.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "balance", expression = "java(card.getAccount().getBalance())")
    CardResponse cardToCardResponse(Card card);
}
