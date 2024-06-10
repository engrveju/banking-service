package com.dot.bankingservice.exception;

public class AccountDetailsNotFoundException extends RuntimeException{

    public AccountDetailsNotFoundException(String message){
        super(message);
    }
}
