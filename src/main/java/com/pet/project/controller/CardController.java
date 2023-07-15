package com.pet.project.controller;

import com.pet.project.model.dto.card.CardBalanceUpdateRequest;
import com.pet.project.model.dto.card.CardMapper;
import com.pet.project.model.dto.card.CardResponse;
import com.pet.project.model.dto.utils.OperationResponse;
import com.pet.project.model.entity.Card;
import com.pet.project.service.AccountService;
import com.pet.project.service.CardService;
import com.pet.project.service.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.pet.project.controller.ControllerStaticHelper.getRole;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/customers/{owner-id}/cards")
public class CardController {
    private final CustomerService customerService;
    private final CardService cardService;
    private final AccountService accountService;
    private final CardMapper mapper;

    @GetMapping
    @PreAuthorize("@authorizationService.isUserAdminOrIsUsersSame(authentication.principal, #ownerId)")
    List<CardResponse> getAll(@PathVariable("owner-id") long ownerId, Authentication authentication) {
        var principal = customerService.loadUserByUsername(authentication.getName());
        var responses = customerService.readById(ownerId).getMyCards()
                .stream()
                .map(mapper::cardToCardResponse)
                .collect(Collectors.toList());

        log.info("=== GET-CARDS/{}-get === auth.name = {}", getRole(principal), principal.getUsername());
        return responses;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.isUserAdminOrValidUserAndIsCardOwner(authentication.principal, #ownerId, #id)")
    CardResponse getCardById(@PathVariable("owner-id") long ownerId, @PathVariable("id") long id, Authentication authentication) {
        var principal = customerService.loadUserByUsername(authentication.getName());
        var response = cardService.readByOwner(customerService.readById(ownerId), id);

        log.info("=== GET-CARD/{}-get === auth.name = {}", getRole(principal), principal.getUsername());
        return mapper.cardToCardResponse(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authorizationService.isUserAdminOrIsUsersSame(authentication.principal, #ownerId)")
    CardResponse create(@PathVariable("owner-id") long ownerId, Authentication authentication) {
        var principal = customerService.loadUserByUsername(authentication.getName());
        var response = new Card();
        accountService.create(response, customerService.readById(ownerId));

        log.info("=== POST-CARD/{}-post === auth.name = {}", getRole(principal), principal.getUsername());
        return mapper.cardToCardResponse(response);
    }

    @PutMapping("{id}")
    @PreAuthorize("@authorizationService.isUserAdminOrValidUserAndIsCardOwner(authentication.principal, #ownerId, #id)")
    CardResponse updateBalance(@PathVariable("owner-id") long ownerId, @PathVariable("id") long id,
                               @RequestBody CardBalanceUpdateRequest request, Authentication authentication) {
        var principal = customerService.loadUserByUsername(authentication.getName());
        var response = cardService.readById(id);
        accountService.replenishBalance(response.getAccount().getId(), request.getSum());

        log.info("=== PUT-CARD/{}-put === auth.name = {}", getRole(principal), principal.getUsername());
        return mapper.cardToCardResponse(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("@authorizationService.isUserAdminOrValidUserAndIsCardOwner(authentication.principal, #ownerId, #id)")
    OperationResponse deleteCard(@PathVariable("owner-id") long ownerId, @PathVariable("id") long id, Authentication authentication) {
        var principal = customerService.loadUserByUsername(authentication.getName());
        var card = cardService.readById(id);
        cardService.delete(id);

        log.info("=== DELETE-CARD/{}-delete === auth.name = {}", getRole(principal), principal.getUsername());
        return OperationResponse.builder()
                .message(customerService.readById(ownerId).getName() + " card with number " + card.getNumber() + " has been deleted!")
                .build();
    }
}
