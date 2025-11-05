package org.example.utilities;


import java.security.SecureRandom;

public class CredentialGenerator {

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();


    public static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }

    public static String generateUsername(String firstName, String lastName, long existingCount) {
        String baseUsername = firstName + "." + lastName;
        if (existingCount > 0) {
            baseUsername += (existingCount + 1);
        }
        return baseUsername;
    }
}
