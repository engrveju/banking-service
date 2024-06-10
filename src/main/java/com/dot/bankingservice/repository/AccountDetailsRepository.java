package com.dot.bankingservice.repository;

import com.dot.bankingservice.models.AccountDetails;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AccountDetails a WHERE a.accountNumber = :accountNumber")
    Optional<AccountDetails> findByAccountNumber(@Param("accountNumber") String accountNumber);
}
