package com.dot.bankingservice.service.serviceImpl;

import com.dot.bankingservice.dtos.TransactionDto;
import com.dot.bankingservice.dtos.request.FundsTransferRequestDto;
import com.dot.bankingservice.dtos.response.ApiResponse;
import com.dot.bankingservice.enums.ResponseCode;
import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.enums.TransactionType;
import com.dot.bankingservice.models.TransactionSummary;
import com.dot.bankingservice.models.Transactions;
import com.dot.bankingservice.repository.TransactionRepository;
import com.dot.bankingservice.repository.TransactionRepositoryCustom;
import com.dot.bankingservice.repository.TransactionSummaryRepository;
import com.dot.bankingservice.service.FeeService;
import com.dot.bankingservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final TransactionRepository transactionRepository;
    private final TransactionRepositoryCustom transactionRepositoryCustom;
    private final TransactionSummaryRepository transactionSummaryRepository;
    private final FeeService feeService;

    @Transactional
    public Transactions logTransaction(TransactionDto transactionDto) {
        Transactions transactions = mapTransactionDtoToEntity(transactionDto, new Transactions());
        log.debug("Saving Transaction log: {}",transactions);
        return transactionRepository.save(transactions);
    }

    @Transactional
    public Transactions updateTransaction(Transactions transactions, TransactionDto transactionDto) {
        mapTransactionDtoToEntity(transactionDto, transactions);
        log.debug("Updating Transaction log: {}",transactions);
        return transactionRepository.save(transactions);
    }

    @Override
    public ResponseEntity<ApiResponse> getTransactions(String accountNumber, TransactionStatus status, String transactionReference, LocalDate startDate, LocalDate endDate, int pageNumber, int pageSize) {
        int size = pageNumber > MAX_PAGE_SIZE ? DEFAULT_PAGE_SIZE : pageSize;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by("updatedAt").descending());
        Page<Transactions> transactions = transactionRepositoryCustom.findTransactions(accountNumber, status, transactionReference, startDate, endDate, pageable);

        Map<String, Object> pagedResponse = new LinkedHashMap<>();
        pagedResponse.put("totalPage", transactions.getTotalPages());
        pagedResponse.put("totalElements", transactions.getTotalElements());
        pagedResponse.put("content", transactions.getContent());

        return new ResponseEntity<>(new ApiResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDescription(), pagedResponse), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> getTransactionSummary(LocalDate startDate, LocalDate endDate, Integer pageNumber, Integer pageSize) {
        startDate = (startDate == null) ? LocalDate.now().minusDays(1) : startDate;
        endDate = (endDate == null) ? LocalDate.now() : endDate;


        List<TransactionSummary> summary = transactionSummaryRepository.findAllByUpdatedAtIsBetween(LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX));
        return new ResponseEntity<>(new ApiResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDescription(), summary), HttpStatus.OK);
    }

    @Scheduled(cron = "${transaction.cron.summary}")
    @Transactional
    public void generateTransactionSummary() {
        try {
            log.info("Generating transaction summary");

            LocalDateTime today = LocalDateTime.of(LocalDate.now(),LocalTime.MAX);
            LocalDateTime previousDay = LocalDateTime.of(LocalDate.now().minusDays(1),LocalTime.MIN);

            List<Transactions> transactions = transactionRepository.findAllByUpdatedAtIsBetweenAndTransactionStatus(previousDay,today,TransactionStatus.SUCCESSFUL);
            TransactionSummary transactionSummary = calculateTransactionSummary(transactions);
            transactionSummaryRepository.save(transactionSummary);
        } catch (Exception e) {
            log.error("Error generating transaction summary", e);
        }
    }

    @Scheduled(cron = "${transaction.cron.commission}")
    @Transactional
    public void updateCommissionWorthyTransactions() {
        try {
            log.info("updating CommissionWorthy Transactions  summary");
            List<Transactions> transactions = transactionRepository.findAllByTransactionStatusAndCommissionWorthy(TransactionStatus.SUCCESSFUL, false,TransactionType.DR);
            transactions.forEach(transaction -> {
                BigDecimal transactionFee = transaction.getTransactionFee();
                BigDecimal commission = feeService.calculateCommission(transactionFee);
                transactionRepository.updateTransactionCommission(transaction.getId(), commission);
            });
        } catch (Exception e) {
            log.error("Error updating commission worthy transactions", e);
        }
    }

    private TransactionSummary calculateTransactionSummary(List<Transactions> transactions) {
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;

        for (Transactions transaction : transactions) {
            if (transaction.getTransactionType().equals(TransactionType.CR)) {
                totalCredit = totalCredit.add(transaction.getAmount());
            }

            if (transaction.getTransactionType().equals(TransactionType.DR)) {
                totalDebit = totalDebit.add(transaction.getBilledAmount());
                totalFees = totalFees.add(transaction.getTransactionFee());
                totalCommission = totalCommission.add(transaction.getCommission());
            }
        }

        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setTotalCommission(totalCommission);
        transactionSummary.setTotalFees(totalFees);
        transactionSummary.setTotalDebit(totalDebit);
        transactionSummary.setTotalCredit(totalCredit);

        return transactionSummary;
    }

    private Transactions mapTransactionDtoToEntity(TransactionDto transactionDto, Transactions transactions) {
        FundsTransferRequestDto transferRequestDto = transactionDto.getTransferRequestDto();

        String accountNumber = transactionDto.getTransactionType().equals(TransactionType.DR) ?
                transferRequestDto.getSourceAccountNumber() : transferRequestDto.getBeneficiaryAccountNumber();

        BigDecimal billedAmount = transactionDto.getTransactionType().equals(TransactionType.DR) ?
                transactionDto.getBilledAmount() : BigDecimal.ZERO;

        transactions.setDescription(transferRequestDto.getDescription());
        transactions.setTransactionReference(transactionDto.getTransactionReference());
        transactions.setAmount(transferRequestDto.getAmount());
        transactions.setTransactionFee(transactionDto.getTransactionFee());
        transactions.setBilledAmount(billedAmount);
        transactions.setCommissionWorthy(false);
        transactions.setCommission(transactionDto.getCommission());
        transactions.setAccountNumber(accountNumber);
        transactions.setStatusMessage(transactionDto.getStatusMessage());
        transactions.setTransactionStatus(transactionDto.getTransactionStatus());
        transactions.setStatusMessage(transactionDto.getStatusMessage());
        transactions.setTransactionType(transactionDto.getTransactionType());
        transactions.setSender(transferRequestDto.getSourceAccountNumber());
        transactions.setReceiver(transferRequestDto.getBeneficiaryAccountNumber());
        transactions.setBalanceBeforeTransaction(transactionDto.getBalanceBeforeTransaction());
        transactions.setBalanceAfterTransaction(transactionDto.getBalanceAfterTransaction());

        return transactions;
    }
}
