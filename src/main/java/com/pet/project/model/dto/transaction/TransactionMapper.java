package com.pet.project.model.dto.transaction;

import com.pet.project.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = java.math.BigDecimal.class)
public interface TransactionMapper {
    @Mapping(target = "fundsWithdrawn", expression = "java(transaction.getFundsWithdrawn().setScale(2, BigDecimal.ROUND_HALF_EVEN))")
    @Mapping(target = "balanceAfter", expression = "java(transaction.getBalanceAfter().setScale(2, BigDecimal.ROUND_HALF_EVEN))")
    TransactionHistoryResponse transactionToTransactionHistoryResponse(Transaction transaction);

    @Mapping(target = "fundsWithdrawn", expression = "java(transaction.getFundsWithdrawn().setScale(2, BigDecimal.ROUND_HALF_EVEN))")
    @Mapping(target = "transferAmount", expression = "java(transaction.getTransferAmount().setScale(2, BigDecimal.ROUND_HALF_EVEN))")
    @Mapping(target = "balanceAfter", expression = "java(transaction.getBalanceAfter().setScale(2, BigDecimal.ROUND_HALF_EVEN))")
    TransactionReadResponse transactionToTransactionReadResponse(Transaction transaction);
}
