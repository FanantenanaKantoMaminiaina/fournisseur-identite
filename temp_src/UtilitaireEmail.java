package util;

import javax.mail.*;

import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.internet.*;


public class UtilitaireEmail {
    public static boolean envoyerEmail(String emailSender,String mdpSender,String email, String htmlString) throws Exception {
        try {
            String var1 = emailSender;  // Adresse de l'expéditeur
            String var2 = mdpSender;  // Mot de passe de l'expéditeur
            String var3 = email;  // Destinataire du mail

            // Configuration des propriétés pour la connexion SMTP avec Gmail
            Properties var4 = new Properties();
            var4.put("mail.smtp.host", "smtp.gmail.com");
            var4.put("mail.smtp.port", "587");
            var4.put("mail.smtp.auth", "true");
            var4.put("mail.smtp.starttls.enable", "true");

            // Authentification de l'expéditeur
            Session var5 = Session.getInstance(var4, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(var1, var2);  // var1 = e-mail, var2 = mot de passe
                }
            });

            // Création de l'objet MimeMessage
            MimeMessage var49 = new MimeMessage(var5);
            var49.setFrom(new InternetAddress(var1));
            var49.setRecipients(Message.RecipientType.TO, InternetAddress.parse(var3));
            var49.setSubject("Email avec HTML, CSS et images intégrées");

            // Création de MimeMultipart avec le type "related"
            MimeMultipart var47 = new MimeMultipart("related");

            // Créer la partie HTML
            MimeBodyPart var48 = new MimeBodyPart();
            var48.setContent(htmlString, "text/html");

            // Ajouter la partie HTML au MimeMultipart
            var47.addBodyPart(var48);

            // Fixer le contenu du message avec le MimeMultipart
            var49.setContent(var47);

            // Envoi de l'email
            Transport.send(var49);
            System.out.println("Email envoyé avec succès !");
            return true;

        } catch (Exception e) {
           throw e;
        }
    }

}
