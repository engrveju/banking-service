package com.dot.bankingservice.controller;

import com.dot.bankingservice.dtos.request.FundsTransferRequestDto;
import com.dot.bankingservice.dtos.response.ApiResponse;
import com.dot.bankingservice.service.BankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/banking")
public class BankingController {
    private final BankingService bankingService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse> fundsTransfer(@Valid @RequestBody FundsTransferRequestDto transferRequestDto){
        log.debug("Inside BankingController.fundsTransfer with transferRequestDto: {}",transferRequestDto);
        return bankingService.transferFunds(transferRequestDto);
    }
}
