package com.dot.bankingservice.enums;


import lombok.Getter;

@Getter
public enum TransactionStatus {

    SUCCESSFUL, INSUFFICIENT_FUNDS, FAILED, PROCESSING;


}
