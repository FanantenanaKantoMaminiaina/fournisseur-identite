package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UtilitaireAuthentification{

    public static String generateRandomToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32]; // Taille en octets (32 octets = 64 caractères hexadécimaux)
        secureRandom.nextBytes(randomBytes);

        // Convertir les octets en hexadécimal
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

        // Ajouter des chiffres aléatoires
        for (int i = 0; i < length; i++) {
            int digit = secureRandom.nextInt(10); // Nombre entre 0 et 9
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
    
    public static boolean isPasswordValid(String passwordHashe,String passwordInput){
        return hashPassword(passwordInput).equals(passwordHashe);
    }

    public  static String extractToken(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        if (headerValue == null || headerValue.isEmpty()) {
            return null; 
        }        
        if (headerValue.startsWith("Bearer ")) {
            return headerValue.substring(7);
        }
        return headerValue;
    }

    public  static  boolean verifyHeader(HttpServletRequest request, HttpServletResponse response, String headerName) throws IOException {
        String headerValue = request.getHeader(headerName);
        if (headerValue == null || headerValue.isEmpty()) {
            response.getWriter().print(ApiResponse.error(401, "Non autorise", "header invalide ou manquant: " + headerName));
            return false; 
        }
        if (!headerValue.startsWith("Bearer ")) {
            response.getWriter().print(ApiResponse.error(401, "Non autorise", "format de token invalide dans l'header: " + headerName));
            return false;
        }
        return true; 
    }
}