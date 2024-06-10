package com.dot.bankingservice.controller;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.service.TransactionService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {TransactionController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class TransactionControllerTest {
    @Autowired
    private TransactionController transactionController;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testGetTransactions() throws Exception {
        when(transactionService.getTransactions(Mockito.<String>any(), Mockito.<TransactionStatus>any(),
                Mockito.<String>any(), Mockito.<LocalDate>any(), Mockito.<LocalDate>any(), anyInt(), anyInt()))
                .thenReturn(null);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transaction");

        MockMvcBuilders.standaloneSetup(transactionController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetTransactionSummary() throws Exception {
        when(transactionService.getTransactionSummary(Mockito.<LocalDate>any(), Mockito.<LocalDate>any(),
                Mockito.<Integer>any(), Mockito.<Integer>any())).thenReturn(null);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transaction/summary");

        MockMvcBuilders.standaloneSetup(transactionController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
