package com.pet.project.controller;

import com.pet.project.model.dto.transaction.TransactionCreateRequest;
import com.pet.project.model.dto.transaction.TransactionHistoryResponse;
import com.pet.project.model.dto.transaction.TransactionMapper;
import com.pet.project.model.dto.transaction.TransactionReadResponse;
import com.pet.project.model.dto.utils.OperationResponse;
import com.pet.project.service.CardService;
import com.pet.project.service.CustomerService;
import com.pet.project.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/customers/{owner-id}/cards/{card-id}/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final CardService cardService;
    private final CustomerService customerService;
    private final TransactionMapper mapper;

    @GetMapping
    @PreAuthorize("@authorizationService.isUserAdminOrValidUserAndIsCardOwner(authentication.principal, #ownerId, #cardId)")
    List<TransactionHistoryResponse> getAll(@PathVariable("owner-id") long ownerId, @PathVariable("card-id") long cardId, Authentication authentication) {
        var principal = customerService.loadUserByUsername(authentication.getName());
        var responses = cardService.getHistory(cardId)
                .stream()
                .map(mapper::transactionToTransactionHistoryResponse)
                .collect(Collectors.toList());

        log.info("=== GET-TRANSACTIONS/{}-get === auth.name = {}", principal.getRole().getName().toLowerCase(), principal.getUsername());
        return responses;
    }

    @GetMapping("{id}")
    @PreAuthorize("@authorizationService.isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransaction(authentication.principal, #ownerId, #cardId, #id)")
    TransactionReadResponse getOne(@PathVariable("owner-id") long ownerId, @PathVariable("card-id") long cardId,
                                   @PathVariable("id") long id, Authentication authentication) {
        var response = transactionService.readById(id);
        var principal = customerService.loadUserByUsername(authentication.getName());

        log.info("=== GET-TRANSACTION/{}-get === auth.name = {}", principal.getRole().getName().toLowerCase(), principal.getUsername());
        return mapper.transactionToTransactionReadResponse(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authorizationService.isUserValidUserAndIsCardOwner(authentication.principal, #ownerId, #cardId)")
    TransactionReadResponse create(@PathVariable("owner-id") long ownerId, @PathVariable("card-id") long cardId,
                                      @RequestBody TransactionCreateRequest request, Authentication authentication) {
        var response = transactionService.create(request, cardService.readById(cardId).getAccount().getId());

        log.info("=== POST-TRANSACTION/{}-post === auth.name = {}", customerService.readById(ownerId).getRole().getName().toLowerCase(), authentication.getPrincipal());
        return mapper.transactionToTransactionReadResponse(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("@authorizationService.isAdmin(authentication.principal)")
    OperationResponse delete(@PathVariable("owner-id") long ownerId, @PathVariable("card-id") long cardId,
                             @PathVariable("id") long id, Authentication authentication) {
        transactionService.delete(id);
        var principal = customerService.loadUserByUsername(authentication.getName());

        log.info("=== DELETE-TRANSACTION/{}-delete === auth.name = {}", principal.getRole().getName().toLowerCase(), principal.getUsername());
        return OperationResponse.builder()
                .message("Transaction for customer " + customerService.readById(ownerId).getName() +
                        " and his/her card " + cardService.readById(cardId).getNumber() + " has been deleted!")
                .build();
    }
}
