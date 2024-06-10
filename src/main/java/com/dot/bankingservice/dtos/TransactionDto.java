package com.dot.bankingservice.dtos;

import com.dot.bankingservice.dtos.request.FundsTransferRequestDto;
import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDto {

    @NotNull
    private FundsTransferRequestDto transferRequestDto;

    @NotBlank
    private String transactionReference;

    @NotNull
    private BigDecimal balanceBeforeTransaction;

    @NotNull
    private BigDecimal balanceAfterTransaction;

    private BigDecimal transactionFee = BigDecimal.ZERO;

    private BigDecimal commission = BigDecimal.ZERO;

    private BigDecimal billedAmount = BigDecimal.ZERO;

    @NotBlank
    private String statusMessage;

    @NotBlank
    private TransactionStatus transactionStatus;

    @NotNull
    private TransactionType transactionType;
}
