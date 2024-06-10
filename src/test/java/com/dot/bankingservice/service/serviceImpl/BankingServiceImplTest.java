package com.dot.bankingservice.service.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.dot.bankingservice.dtos.TransactionDto;
import com.dot.bankingservice.dtos.request.FundsTransferRequestDto;
import com.dot.bankingservice.dtos.response.ApiResponse;
import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.enums.TransactionType;
import com.dot.bankingservice.exception.AccountDetailsNotFoundException;
import com.dot.bankingservice.models.AccountDetails;
import com.dot.bankingservice.models.Transactions;
import com.dot.bankingservice.service.AccountService;
import com.dot.bankingservice.service.FeeService;
import com.dot.bankingservice.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {BankingServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class BankingServiceImplTest {
    @MockBean
    private AccountService accountService;

    @Autowired
    private BankingServiceImpl bankingServiceImpl;

    @MockBean
    private FeeService feeService;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testTransferFundsWithInsufficientFunds() {

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountBalance(new BigDecimal("1000"));
        accountDetails.setAccountNumber("3063200282");
        accountDetails.setFirstName("Emmanuel");
        accountDetails.setId(1L);
        accountDetails.setLastName("Ugwueze");
        when(accountService.getAccountDetails(Mockito.<String>any())).thenReturn(accountDetails);
        when(feeService.calculateTransactionFee(Mockito.<BigDecimal>any())).thenReturn(new BigDecimal("2000"));

        Transactions transactions = new Transactions();
        transactions.setAccountNumber("3063200282");
        transactions.setAmount(new BigDecimal("3000"));
        transactions.setBalanceAfterTransaction(new BigDecimal("2000"));
        transactions.setBalanceBeforeTransaction(new BigDecimal("1000"));
        transactions.setBilledAmount(new BigDecimal("1012"));
        transactions.setCommission(new BigDecimal("0"));
        transactions.setCommissionWorthy(false);
        transactions.setCreatedAt(LocalDate.of(2024, 6, 8).atStartOfDay());
        transactions.setDescription("Testing insufficient funds");
        transactions.setId(1L);
        transactions.setReceiver("2070101292");
        transactions.setSender("3063200282");
        transactions.setStatusMessage("Request Failed");
        transactions.setTransactionFee(new BigDecimal("2.3"));
        transactions.setTransactionReference("239289378923");
        transactions.setTransactionStatus(TransactionStatus.FAILED);
        transactions.setTransactionType(TransactionType.DR);
        transactions.setUpdatedAt(LocalDate.of(2024, 6, 8).atStartOfDay());
        when(transactionService.logTransaction(Mockito.<TransactionDto>any())).thenReturn(transactions);

        FundsTransferRequestDto transferRequestDto = new FundsTransferRequestDto();
        transferRequestDto.setAmount(new BigDecimal("3000"));
        transferRequestDto.setBeneficiaryAccountNumber("2070101292");
        transferRequestDto.setDescription("Testing insufficient funds");
        transferRequestDto.setSourceAccountNumber("3063200282");


        ResponseEntity<ApiResponse> actualTransferFundsResult = bankingServiceImpl.transferFunds(transferRequestDto);


        verify(accountService, atLeast(1)).getAccountDetails(eq("3063200282"));
        verify(feeService).calculateTransactionFee(Mockito.<BigDecimal>any());
        verify(transactionService).logTransaction(Mockito.<TransactionDto>any());
        ApiResponse body = actualTransferFundsResult.getBody();
        assertEquals("99", body.getStatus());
        assertEquals("INSUFFICIENT_FUNDS", body.getMessage());
        assertEquals(200, actualTransferFundsResult.getStatusCodeValue());
        assertTrue(actualTransferFundsResult.hasBody());
        assertTrue(actualTransferFundsResult.getHeaders().isEmpty());
    }


    @Test
    void testTransferFundsWithAccountNotFound() {
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountBalance(new BigDecimal("5000"));
        accountDetails.setAccountNumber("3063200282");
        accountDetails.setFirstName("Emmanuel");
        accountDetails.setId(1L);
        accountDetails.setLastName("Ugwueze");

        when(accountService.getAccountDetails(argThat(s -> !s.equals("3063200282"))))
                .thenThrow(new AccountDetailsNotFoundException("Invalid Source Account Number"));

        FundsTransferRequestDto transferRequestDto = new FundsTransferRequestDto();
        transferRequestDto.setAmount(new BigDecimal("300"));
        transferRequestDto.setBeneficiaryAccountNumber("2070101292");
        transferRequestDto.setDescription("Testing invalid account");
        transferRequestDto.setSourceAccountNumber("3063200");

        ResponseEntity<ApiResponse> actualTransferFundsResult = bankingServiceImpl.transferFunds(transferRequestDto);

        verify(accountService, atLeast(1)).getAccountDetails(eq("3063200"));

        ApiResponse body = actualTransferFundsResult.getBody();
        assertNotNull(body);
        assertEquals("99", body.getStatus());
        assertEquals("Invalid Source Account Number", body.getMessage());
        assertEquals(200, actualTransferFundsResult.getStatusCodeValue());
        assertTrue(actualTransferFundsResult.hasBody());
        assertTrue(actualTransferFundsResult.getHeaders().isEmpty());
    }


    @Test
    void testTransferFundsSuccessfully() {
        AccountDetails accountDetails = mock(AccountDetails.class);
        accountDetails.setAccountBalance(new BigDecimal("5000"));
        accountDetails.setAccountNumber("3063200282");
        accountDetails.setFirstName("Emmanuel");
        accountDetails.setId(1L);
        accountDetails.setLastName("Ugwueze");

        when(accountDetails.getAccountBalance()).thenReturn(new BigDecimal("5000.00"));



        AccountDetails accountDetails2 = mock(AccountDetails.class);
        AccountDetails accountDetails3 = mock(AccountDetails.class);

        when(accountService.creditAccount(any(), any())).thenReturn(accountDetails3);
        when(accountService.debitAccount(any(), any())).thenReturn(accountDetails2);
        when(accountService.getAccountDetails(any())).thenReturn(accountDetails);
        when(feeService.calculateTransactionFee(BigDecimal.valueOf(1000))).thenReturn(BigDecimal.valueOf(5));


        Transactions transactions = mock(Transactions.class);
        Transactions transactions2 = mock(Transactions.class);

        when(transactionService.updateTransaction(any(), any())).thenReturn(transactions2);
        when(transactionService.logTransaction(any())).thenReturn(transactions);


        FundsTransferRequestDto transferRequestDto = new FundsTransferRequestDto();
        transferRequestDto.setAmount(new BigDecimal("1000"));
        transferRequestDto.setBeneficiaryAccountNumber("2070101292");
        transferRequestDto.setDescription("Test successful Transfer");
        transferRequestDto.setSourceAccountNumber("3063200282");

        ResponseEntity<ApiResponse> actualTransferFundsResult = bankingServiceImpl.transferFunds(transferRequestDto);

        verify(accountDetails, atLeast(1)).getAccountBalance();
        verify(accountDetails).setAccountBalance(any());
        verify(accountDetails).setAccountNumber("3063200282");
        verify(accountDetails).setFirstName("Emmanuel");
        verify(accountDetails).setId(any());
        verify(accountDetails).setLastName("Ugwueze");
        verify(accountService).creditAccount(any(), any());
        verify(accountService).debitAccount(any(), any());
        verify(accountService, atLeast(1)).getAccountDetails("3063200282");
        verify(feeService).calculateTransactionFee(BigDecimal.valueOf(1000));
        verify(transactionService, atLeast(2)).logTransaction(any());
        verify(transactionService).updateTransaction(any(), any());

        ApiResponse body = actualTransferFundsResult.getBody();
        assertEquals("00", body.getStatus());
        assertEquals("Request completed successfully", body.getMessage());
        assertEquals(200, actualTransferFundsResult.getStatusCodeValue());
        assertTrue(actualTransferFundsResult.hasBody());
        assertTrue(actualTransferFundsResult.getHeaders().isEmpty());
    }


}
