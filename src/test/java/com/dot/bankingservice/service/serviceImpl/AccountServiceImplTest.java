package com.dot.bankingservice.service.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.dot.bankingservice.models.AccountDetails;
import com.dot.bankingservice.repository.AccountDetailsRepository;
import com.dot.bankingservice.service.TransactionService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AccountServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class AccountServiceImplTest {
    @MockBean
    private AccountDetailsRepository accountDetailsRepository;

    @Autowired
    private AccountServiceImpl accountServiceImpl;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testGetAccountDetails() {

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountBalance(new BigDecimal("5000"));
        accountDetails.setAccountNumber("3063200282");
        accountDetails.setFirstName("Emmanuel");
        accountDetails.setId(1L);
        accountDetails.setLastName("Ugwueze");

        Optional<AccountDetails> ofResult = Optional.of(accountDetails);
        when(accountDetailsRepository.findByAccountNumber("3063200282")).thenReturn(ofResult);
        when(accountDetailsRepository.findByAccountNumber("306")).thenReturn(Optional.empty());

        AccountDetails actualAccountDetails = accountServiceImpl.getAccountDetails("3063200282");
        AccountDetails actualEmptyResult = accountServiceImpl.getAccountDetails("306");

        verify(accountDetailsRepository).findByAccountNumber(eq("3063200282"));
        assertSame(accountDetails, actualAccountDetails);
        assertNull(actualEmptyResult);
    }

    @Test
    void testDebitAccount() {

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountBalance(new BigDecimal("5000"));
        accountDetails.setAccountNumber("3063200282");
        accountDetails.setFirstName("Emmanuel");
        accountDetails.setId(1L);
        accountDetails.setLastName("Ugwueze");
        when(accountDetailsRepository.save(Mockito.<AccountDetails>any())).thenReturn(accountDetails);


        AccountDetails actualDebitAccountResult = accountServiceImpl.debitAccount(accountDetails, new BigDecimal("2000"));


        verify(accountDetailsRepository).save(Mockito.<AccountDetails>any());
        BigDecimal expectedAccountBalance = new BigDecimal("3000");
        assertEquals(expectedAccountBalance, accountDetails.getAccountBalance());
        assertSame(accountDetails, actualDebitAccountResult);
    }


    @Test
    void testCreditAccount() {

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountBalance(new BigDecimal("0"));
        accountDetails.setAccountNumber("2070101292");
        accountDetails.setFirstName("James");
        accountDetails.setId(1L);
        accountDetails.setLastName("Peter");
        when(accountDetailsRepository.save(Mockito.<AccountDetails>any())).thenReturn(accountDetails);

        AccountDetails actualCreditAccountResult = accountServiceImpl.creditAccount(accountDetails, new BigDecimal("3000"));

        verify(accountDetailsRepository).save(Mockito.<AccountDetails>any());
        BigDecimal expectedAccountBalance = new BigDecimal("3000");
        assertEquals(expectedAccountBalance, accountDetails.getAccountBalance());
        assertSame(accountDetails, actualCreditAccountResult);
    }
}
