package com.dot.bankingservice.service.serviceImpl;

import com.dot.bankingservice.dtos.ProcessFundsDto;
import com.dot.bankingservice.dtos.TransactionDto;
import com.dot.bankingservice.dtos.request.FundsTransferRequestDto;
import com.dot.bankingservice.dtos.response.ApiResponse;
import com.dot.bankingservice.enums.ResponseCode;
import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.enums.TransactionType;
import com.dot.bankingservice.exception.AccountDetailsNotFoundException;
import com.dot.bankingservice.models.AccountDetails;
import com.dot.bankingservice.models.Transactions;
import com.dot.bankingservice.service.AccountService;
import com.dot.bankingservice.service.BankingService;
import com.dot.bankingservice.service.FeeService;
import com.dot.bankingservice.service.TransactionService;
import com.dot.bankingservice.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankingServiceImpl implements BankingService {
    private final AccountService accountService;
    private final FeeService feeService;
    private final TransactionService transactionService;

    @Transactional
    public ResponseEntity<ApiResponse> transferFunds(FundsTransferRequestDto transferRequestDto) {
        ApiResponse response = new ApiResponse();
        response.setStatus(ResponseCode.FAILED.getCode());
        response.setMessage(ResponseCode.FAILED.getDescription());

        try {
            AccountDetails sourceAccountDetails = accountService.getAccountDetails(transferRequestDto.getSourceAccountNumber());
            if(sourceAccountDetails==null){
                log.debug("Invalid Source Account Number");
                throw new AccountDetailsNotFoundException("Invalid Source Account Number");
            }

            AccountDetails destinationAccountDetails = accountService. getAccountDetails(transferRequestDto.getBeneficiaryAccountNumber());
            if (destinationAccountDetails == null) {
                String message = "Invalid Beneficiary Account";
                response.setMessage(message);
                log.debug(message);
                return new ResponseEntity<>(response,HttpStatus.OK);
            }

            BigDecimal transactionAmount = transferRequestDto.getAmount();
            BigDecimal transactionFee = feeService.calculateTransactionFee(transactionAmount);
            BigDecimal totalAmount = transactionAmount.add(transactionFee);
            String transactionReference = AppUtils.generateTransactionReference();

            TransactionDto transactionDto = createTransactionDto(transferRequestDto, transactionReference, sourceAccountDetails);

            if (sourceAccountDetails.getAccountBalance().compareTo(totalAmount) < 0) {
                String message = TransactionStatus.INSUFFICIENT_FUNDS.name();
                transactionDto.setTransactionStatus(TransactionStatus.FAILED);
                transactionDto.setStatusMessage(message);
                transactionService.logTransaction(transactionDto);
                response.setMessage(message);
                log.debug("Error processing transfer for accountDetails {}, message: {}",sourceAccountDetails,message);
                return new ResponseEntity<>(response,HttpStatus.OK);
            }

            Transactions transactions = transactionService.logTransaction(transactionDto);

            transactionDto.setTransactionFee(transactionFee);
            transactionDto.setBilledAmount(totalAmount);

            ProcessFundsDto processFundsDto = new ProcessFundsDto();
            processFundsDto.setTransactionDto(transactionDto);
            processFundsDto.setTransactions(transactions);
            processFundsDto.setSourceAccountDetails(sourceAccountDetails);
            processFundsDto.setDestinationAccountDetails(destinationAccountDetails);
            processFundsDto.setTotalFee(transactionFee);
            processFundsDto.setTransactionAmount(transactionAmount);

            processFundsTransfer(processFundsDto);

            response.setStatus(ResponseCode.SUCCESS.getCode());
            response.setMessage(ResponseCode.SUCCESS.getDescription());
            return new ResponseEntity<>(response,HttpStatus.OK);

        } catch (AccountDetailsNotFoundException e) {
            log.error("Error during fund transfer", e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    private TransactionDto createTransactionDto(FundsTransferRequestDto transferRequestDto, String transactionReference, AccountDetails sourceAccountDetails) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTransferRequestDto(transferRequestDto);
        transactionDto.setTransactionFee(BigDecimal.ZERO);
        transactionDto.setBilledAmount(BigDecimal.ZERO);
        transactionDto.setCommission(BigDecimal.ZERO);
        transactionDto.setTransactionType(TransactionType.DR);
        transactionDto.setBalanceBeforeTransaction(sourceAccountDetails.getAccountBalance());
        transactionDto.setBalanceAfterTransaction(sourceAccountDetails.getAccountBalance());
        transactionDto.setTransactionReference(transactionReference);
        transactionDto.setTransactionStatus(TransactionStatus.PROCESSING);
        transactionDto.setStatusMessage(TransactionStatus.PROCESSING.name());
        return transactionDto;
    }


    private void processFundsTransfer(ProcessFundsDto processFundsDto) {
        TransactionDto transactionDto = processFundsDto.getTransactionDto();
        AccountDetails sourceAccountDetails = processFundsDto.getSourceAccountDetails();
        AccountDetails destinationAccountDetails = processFundsDto.getDestinationAccountDetails();

        BigDecimal amountToBeDebited = processFundsDto.getTransactionAmount().add(processFundsDto.getTotalFee());
        BigDecimal balanceBeforeDebit = sourceAccountDetails.getAccountBalance();
        AccountDetails sourceAccount = accountService.debitAccount(sourceAccountDetails,amountToBeDebited);

        transactionDto.setTransactionType(TransactionType.DR);
        transactionDto.setBalanceBeforeTransaction(balanceBeforeDebit);
        transactionDto.setBalanceAfterTransaction(sourceAccount.getAccountBalance());
        transactionDto.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        transactionDto.setStatusMessage(TransactionStatus.SUCCESSFUL.name());

        transactionService.updateTransaction(processFundsDto.getTransactions(),transactionDto);

        BigDecimal balanceBeforeCredit = destinationAccountDetails.getAccountBalance();
        AccountDetails destinationAccount = accountService.creditAccount(destinationAccountDetails,processFundsDto.getTransactionAmount());

        transactionDto.setTransactionType(TransactionType.CR);
        transactionDto.setTransactionFee(BigDecimal.ZERO);
        transactionDto.setBalanceBeforeTransaction(balanceBeforeCredit);
        transactionDto.setBalanceAfterTransaction(destinationAccount.getAccountBalance());
        transactionDto.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        transactionDto.setStatusMessage(TransactionStatus.SUCCESSFUL.name());
        transactionService.logTransaction(transactionDto);
    }

}