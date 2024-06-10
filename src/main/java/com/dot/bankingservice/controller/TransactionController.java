package com.dot.bankingservice.controller;

import com.dot.bankingservice.dtos.response.ApiResponse;
import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse> getTransactions(@RequestParam(required = false)TransactionStatus status,
                                                       @RequestParam(required = false)String accountNumber,
                                                       @RequestParam(required = false) String transactionReference,
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                       @RequestParam(required = false,defaultValue = "0") Integer pageNumber,
                                                       @RequestParam(required = false, defaultValue = "20") Integer pageSize
                                                       ){
        log.debug("Inside TransactionController.getTransactions with Request params: status={}, accountNumber={}, transactionReference={}, startDate={}, endDate={}, pageNumber={}, pageSize={}", status, accountNumber, transactionReference, startDate, endDate, pageNumber, pageSize);
        return transactionService.getTransactions(accountNumber,status,transactionReference,startDate,endDate,pageNumber,pageSize);
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getTransactionSummary(
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                       @RequestParam(required = false,defaultValue = "0") Integer pageNumber,
                                                       @RequestParam(required = false, defaultValue = "20") Integer pageSize
    ){
        log.debug("Inside TransactionController.getTransactionSummary with Request params: startDate={}, endDate={}, pageNumber={}, pageSize={}",  startDate, endDate, pageNumber, pageSize);
        return transactionService.getTransactionSummary(startDate,endDate,pageNumber,pageSize);
    }
}
