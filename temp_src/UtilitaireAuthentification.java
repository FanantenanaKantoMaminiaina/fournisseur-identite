package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class UtilitaireAuthentification {


    public static String generateRandomToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32]; 
        secureRandom.nextBytes(randomBytes);
        StringBuilder hexString = new StringBuilder();
        for (byte b : randomBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static String generatePin(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("La longueur du PIN doit être positive.");
        }
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = secureRandom.nextInt(10); 
            pin.append(digit);
        }
        return pin.toString();
    }   

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur : Algorithme SHA-256 introuvable", e);
        }
    }
    
    public boolean isPasswordValid(String passwordInput,String passwordHashe){
        return hashPassword(passwordInput).equals(passwordHashe);
    }
}


