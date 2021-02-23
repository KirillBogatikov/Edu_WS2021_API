package org.ws2021.util;

import java.util.Random;

public class BookCodeGenerator {
    private static final String ALPHABET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnPpQqRrSsTtUuVvWwXxYyZz";
    private static final int LENGTH = 8;
    private static final Random random = new Random();
    
    public static String generate() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            int point = random.nextInt(LENGTH);
            code.append(ALPHABET.charAt(point));
        }
        
        return code.toString();
    }
}
