package com.dot.bankingservice.service.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeeServiceImplTest {

    private FeeServiceImpl feeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        feeService = new FeeServiceImpl();
        ReflectionTestUtils.setField(feeService, "FEE_RATE", new BigDecimal("0.005"));
        ReflectionTestUtils.setField(feeService, "MAX_FEE", new BigDecimal("100.00"));
        ReflectionTestUtils.setField(feeService, "COMMISSION_RATE", new BigDecimal("0.2"));
    }

    @Test
    void calculateTransactionFee_whenFeeIsBelowMax() {
        BigDecimal transactionAmount = new BigDecimal("10000");
        BigDecimal expectedFee = new BigDecimal("50.00");
        BigDecimal actualFee = feeService.calculateTransactionFee(transactionAmount);

        assertEquals(expectedFee.setScale(2, RoundingMode.HALF_UP),
                actualFee.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void calculateTransactionFee_whenFeeIsAboveMax() {
        BigDecimal transactionAmount = new BigDecimal("30000");
        BigDecimal expectedFee = new BigDecimal("100.00");
        BigDecimal actualFee = feeService.calculateTransactionFee(transactionAmount);

        assertEquals(expectedFee.setScale(2, RoundingMode.HALF_UP),
                actualFee.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void calculateCommission() {
        BigDecimal transactionFee = new BigDecimal("50.00");
        BigDecimal expectedCommission = new BigDecimal("10.00");
        BigDecimal actualCommission = feeService.calculateCommission(transactionFee);

        assertEquals(expectedCommission.setScale(2, RoundingMode.HALF_UP),
                actualCommission.setScale(2, RoundingMode.HALF_UP));
    }
}
