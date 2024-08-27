package com.example.runningweb.util;

import java.security.SecureRandom;

public class RandomEmailCodeGenerator {

    public static final int CODE_LENGTH = 6;
    public static final int PASSWORD_LENGTH = 12;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        String generateCode = sb.toString();
        return generateCode;
    }

}
