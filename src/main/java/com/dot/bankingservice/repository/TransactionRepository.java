package com.dot.bankingservice.repository;

import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.enums.TransactionType;
import com.dot.bankingservice.models.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions,Long> {

    @Query("SELECT t FROM Transactions t WHERE " +
            "(:accountNumber IS NULL OR t.accountNumber = :accountNumber) AND " +
            "(:status IS NULL OR t.transactionStatus = :status) AND " +
            "(:transactionReference IS NULL OR t.transactionReference = :transactionReference) AND " +
            "(:startDate IS NULL OR t.updatedAt >= :startDate) AND " +
            "(:endDate IS NULL OR t.updatedAt <= :endDate)")
    Page<Transactions> findTransactions(String accountNumber, TransactionStatus status, String transactionReference, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT t FROM Transactions t WHERE t.transactionStatus = :status AND t.commissionWorthy = :commissionWorthy AND t.transactionType = :transactionType")
    List<Transactions> findAllByTransactionStatusAndCommissionWorthy(@Param("status") TransactionStatus status, @Param("commissionWorthy") boolean commissionWorthy, @Param("transactionType") TransactionType transactionType);


    @Modifying
    @Transactional
    @Query("UPDATE Transactions t SET t.commission = :commission, t.commissionWorthy = true WHERE t.id = :id")
    void updateTransactionCommission(@Param("id") Long id, @Param("commission") BigDecimal commission);


    List<Transactions> findAllByUpdatedAtIsBetweenAndTransactionStatus(LocalDateTime start, LocalDateTime end, TransactionStatus status);
}
