package com.dot.bankingservice.service.serviceImpl;

import com.dot.bankingservice.service.FeeService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Setter
public class FeeServiceImpl implements FeeService {

    @Value("${transfer.fee.rate}")
    private BigDecimal FEE_RATE;

    @Value("${transfer.fee.max}")
    private BigDecimal MAX_FEE;

    @Value("${transfer.commission.rate}")
    private BigDecimal COMMISSION_RATE;

    @Override
    public BigDecimal calculateTransactionFee(BigDecimal transactionAmount) {
        BigDecimal transactionFee = transactionAmount.multiply(FEE_RATE);
        return transactionFee.compareTo(MAX_FEE) > 0 ? MAX_FEE : transactionFee;
    }

    @Override
    public BigDecimal calculateCommission(BigDecimal transactionFee) {
        return transactionFee.multiply(COMMISSION_RATE);
    }
}
