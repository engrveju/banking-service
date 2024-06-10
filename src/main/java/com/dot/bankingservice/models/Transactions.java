package com.dot.bankingservice.models;

import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String transactionReference;

    private BigDecimal amount;

    private BigDecimal transactionFee;

    private BigDecimal billedAmount;

    private String description;

    private boolean commissionWorthy;

    private BigDecimal commission;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String sender;

    @NotBlank
    private String receiver;

    private BigDecimal balanceBeforeTransaction;

    private BigDecimal balanceAfterTransaction;

    private String statusMessage;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
