package com.dot.bankingservice.utils;

import java.security.SecureRandom;

public class AppUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String NUMERIC_CHARACTERS = "0123456789";
    private static final int LENGTH = 20;

    public static String generateTransactionReference() {
        StringBuilder sb = new StringBuilder(LENGTH);
        int bound = NUMERIC_CHARACTERS.length();

        for (int i = 0; i < LENGTH; i++) {
            int index = secureRandom.nextInt(bound);
            sb.append(NUMERIC_CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
