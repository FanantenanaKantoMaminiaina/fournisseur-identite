package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
// import java.util.Properties;
// import jakarta.mail.*;
// import jakarta.mail.internet.*;

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

    // private void sendPinByEmail(String senderEmail, String senderPassword, String recipientEmail, String pinCode) {
    //     final String host = "smtp.gmail.com";
    //     final int port = 587;

    //     Properties props = new Properties();
    //     props.put("mail.smtp.auth", "true");
    //     props.put("mail.smtp.starttls.enable", "true");
    //     props.put("mail.smtp.host", host);
    //     props.put("mail.smtp.port", port);

    //     Session session = Session.getInstance(props, new Authenticator() {
    //         protected PasswordAuthentication getPasswordAuthentication() {
    //             return new PasswordAuthentication(senderEmail, senderPassword);
    //         }
    //     });

    //     try {
    //         Message message = new MimeMessage(session);
    //         message.setFrom(new InternetAddress(senderEmail));
    //         message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
    //         message.setSubject("Votre code PIN d'authentification");
    //         message.setText("Bonjour,\n\nVotre code PIN est : " + pinCode + "\n\nMerci!");

    //         Transport.send(message);
    //         System.out.println("Email envoyé avec succès à " + recipientEmail);
    //     } catch (MessagingException e) {
    //         e.printStackTrace();
    //         System.out.println("Échec de l'envoi de l'email.");
    //     }
    // }
}