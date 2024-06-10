package com.dot.bankingservice.controller;

import static org.mockito.Mockito.when;

import com.dot.bankingservice.dtos.request.FundsTransferRequestDto;
import com.dot.bankingservice.service.BankingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {BankingController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class BankingControllerTest {
    @Autowired
    private BankingController bankingController;

    @MockBean
    private BankingService bankingService;

    @Test
    void testFundsTransfer() throws Exception {

        when(bankingService.transferFunds(Mockito.<FundsTransferRequestDto>any())).thenReturn(null);

        FundsTransferRequestDto fundsTransferRequestDto = new FundsTransferRequestDto();
        fundsTransferRequestDto.setAmount(new BigDecimal("2000"));
        fundsTransferRequestDto.setBeneficiaryAccountNumber("2070101292");
        fundsTransferRequestDto.setDescription("Testing with Junit");
        fundsTransferRequestDto.setSourceAccountNumber("3063200282");
        String content = (new ObjectMapper()).writeValueAsString(fundsTransferRequestDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/banking/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        MockMvcBuilders.standaloneSetup(bankingController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
