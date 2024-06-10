package com.dot.bankingservice.service;

import com.dot.bankingservice.dtos.request.FundsTransferRequestDto;
import com.dot.bankingservice.dtos.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface BankingService {
    ResponseEntity<ApiResponse> transferFunds(FundsTransferRequestDto transferRequestDto);
}
