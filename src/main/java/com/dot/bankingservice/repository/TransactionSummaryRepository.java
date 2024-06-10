package com.dot.bankingservice.repository;

import com.dot.bankingservice.models.TransactionSummary;
import com.dot.bankingservice.models.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionSummaryRepository extends JpaRepository<TransactionSummary,Long> {

    @Query("SELECT t FROM TransactionSummary t WHERE t.updatedAt BETWEEN :startDate AND :endDate")
    List<TransactionSummary> findAllByUpdatedAtIsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
