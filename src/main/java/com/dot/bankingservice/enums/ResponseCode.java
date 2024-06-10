package com.dot.bankingservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public enum ResponseCode {
    SUCCESS("00", "Request completed successfully"),
    FAILED("99", "Request Failed");

    private String code;
    private String description;



}
