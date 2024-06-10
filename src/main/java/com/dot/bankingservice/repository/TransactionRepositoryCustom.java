package com.dot.bankingservice.repository;

import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.models.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TransactionRepositoryCustom {
    Page<Transactions> findTransactions(String accountNumber, TransactionStatus status, String transactionReference, LocalDate startDate, LocalDate endDate, Pageable pageable);

}
