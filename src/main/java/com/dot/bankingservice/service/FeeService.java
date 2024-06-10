package com.dot.bankingservice.service;

import java.math.BigDecimal;

public interface FeeService {
    BigDecimal calculateTransactionFee(BigDecimal transactionAmount);

    BigDecimal calculateCommission(BigDecimal transactionFee);
}
