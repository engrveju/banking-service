package com.dot.bankingservice.service;

import com.dot.bankingservice.dtos.TransactionDto;
import com.dot.bankingservice.models.AccountDetails;
import com.dot.bankingservice.models.Transactions;

import java.math.BigDecimal;

public interface AccountService {

    AccountDetails getAccountDetails(String accountNumber);

    AccountDetails debitAccount(AccountDetails accountDetails, BigDecimal amount);

    AccountDetails creditAccount(AccountDetails accountDetails, BigDecimal amount);

}
