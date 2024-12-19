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
        byte[] randomBytes = new byte[32]; // Taille en octets (32 octets = 64 caractères hexadecimaux)
        secureRandom.nextBytes(randomBytes);

        // Convertir les octets en hexadecimal
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

        // Ajouter des chiffres aleatoires
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

    public static String getHtmlPin(String pin,int duree_vie_pin){
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Verification du PIN</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }"
                + ".container { background-color: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); max-width: 600px; margin: auto; }"
                + "h1 { color: #333333; }"
                + "p { color: #555555; }"
                + ".pin-box { background-color: #007bff; color: white; font-size: 24px; padding: 10px; text-align: center; border-radius: 5px; }"
                + "a { color: #007bff; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<h1>Verification de votre compte</h1>"
                + "<p>Bonjour,</p>"
                + "<p>Pour continuer, veuillez entrer le code PIN suivant :</p>"
                + "<div class='pin-box'><strong>" + pin + "</strong></div>"
                + "<p>Ce code PIN est valide pendant "+ duree_vie_pin +" secondes. Si vous ne l'avez pas demande, veuillez ignorer cet email.</p>"
                + "<p>Merci,</p>"
                + "</div>"
                + "</body>"
                + "</html>";
        return htmlContent;
    }

    public static String getHtmlResetTentative(String lien) {
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Reinitialisation de tentative</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }"
                + ".container { background-color: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); max-width: 600px; margin: auto; }"
                + "h1 { color: #333333; }"
                + "p { color: #555555; }"
                + ".reset-link { display: block; background-color: #007bff; color: white; text-align: center; padding: 10px; border-radius: 5px; text-decoration: none; }"
                + ".reset-link:hover { background-color: #0056b3; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<h1>Reinitialisation de tentative</h1>"
                + "<p>Bonjour,</p>"
                + "<p>Nous avons detecte une tentative de connexion infructueuse a votre compte.</p>"
                + "<p>Pour reinitialiser la tentative, veuillez cliquer sur le lien ci-dessous :</p>"
                + "<a href=\"" + lien + "\" class=\"reset-link\">Reinitialiser la tentative</a>"
                + "<p>Lien postman : <strong style=\"color: red\">" + lien + "</strong></p>"
                + "<p>Si vous n'avez pas demande cette reinitialisation, ignorez cet email.</p>"
                + "<p>Merci,</p>"
                + "<p>L'equipe [Nom de votre application]</p>"
                + "</div>"
                + "</body>"
                + "</html>";
        return htmlContent;
    }

    public static String getHtmlValidationCompte(String lien) {
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Validation de votre compte</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }"
                + ".container { background-color: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); max-width: 600px; margin: auto; }"
                + "h1 { color: #333333; }"
                + "p { color: #555555; }"
                + ".validation-link { display: block; background-color: #007bff; color: white; text-align: center; padding: 10px; border-radius: 5px; text-decoration: none; }"
                + ".validation-link:hover { background-color: #0056b3; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<h1>Validation de votre compte</h1>"
                + "<p>Bonjour,</p>"
                + "<p>Pour valider votre compte, veuillez cliquer sur le lien ci-dessous :</p>"
                + "<a href=\"" + lien + "\" class=\"validation-link\">Valider le compte</a>"
                + "<p>Lien postman : <strong style=\"color: red\">" + lien + "</strong></p>"
                + "<p>Si vous n'avez pas demande cette validation, veuillez ignorer cet email.</p>"
                + "<p>Merci,</p>"
                + "<p>L'equipe [Nom de votre application]</p>"
                + "</div>"
                + "</body>"
                + "</html>";
        return htmlContent;
}

}