package com.dot.bankingservice.service.serviceImpl;

import com.dot.bankingservice.models.AccountDetails;
import com.dot.bankingservice.repository.AccountDetailsRepository;
import com.dot.bankingservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountDetailsRepository accountDetailsRepository;

    @Override
    public AccountDetails getAccountDetails(String accountNumber) {

        return accountDetailsRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    @Override
    public AccountDetails debitAccount(AccountDetails accountDetails, BigDecimal totalAmountToBeDebited) {

        BigDecimal currentBalance = accountDetails.getAccountBalance();
        BigDecimal amountAfterDebit = currentBalance.subtract(totalAmountToBeDebited);

        accountDetails.setAccountBalance(amountAfterDebit);
        return accountDetailsRepository.save(accountDetails);
    }

    @Override
    public AccountDetails creditAccount(AccountDetails accountDetails, BigDecimal totalAmountToBeCredited) {
        BigDecimal currentBalance = accountDetails.getAccountBalance();
        BigDecimal amountAfterCredit = currentBalance.add(totalAmountToBeCredited);

        accountDetails.setAccountBalance(amountAfterCredit);

        return accountDetailsRepository.save(accountDetails);

    }
}
