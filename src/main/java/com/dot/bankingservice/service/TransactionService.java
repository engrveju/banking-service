package com.dot.bankingservice.service;

import com.dot.bankingservice.dtos.TransactionDto;
import com.dot.bankingservice.dtos.response.ApiResponse;
import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.models.Transactions;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TransactionService {
    Transactions logTransaction(TransactionDto transactionDto);

    Transactions updateTransaction(Transactions transactions,TransactionDto transactionDto);

    ResponseEntity<ApiResponse> getTransactions(String accountNumber, TransactionStatus status, String transactionReference, LocalDate startDate, LocalDate endDate, int pageNumber, int pageSize);

    ResponseEntity<ApiResponse> getTransactionSummary(LocalDate startDate, LocalDate endDate, Integer pageNumber, Integer pageSize);
}
