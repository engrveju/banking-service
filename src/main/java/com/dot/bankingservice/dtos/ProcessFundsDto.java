package com.dot.bankingservice.dtos;

import com.dot.bankingservice.models.AccountDetails;
import com.dot.bankingservice.models.Transactions;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessFundsDto {
    private AccountDetails sourceAccountDetails;
    private AccountDetails destinationAccountDetails;
    private TransactionDto transactionDto;
    private Transactions transactions;
    private BigDecimal transactionAmount;
    private BigDecimal totalFee;
}
