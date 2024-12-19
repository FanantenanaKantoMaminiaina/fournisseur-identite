package util;

import javax.mail.*;

import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.internet.*;


public class UtilitaireEnvoieEmail {
    public static boolean envoyerEmail(String emailExpediteur, String passwordExpediteur, String email, String htmlString) throws Exception {
        try {
            // Configuration des proprietes pour la connexion SMTP avec Gmail
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            // Authentification de l'expediteur
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailExpediteur, passwordExpediteur);
                }
            });

            // Creation de l'objet MimeMessage
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailExpediteur));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Email avec HTML, CSS et images integrees");

            // Creation de MimeMultipart avec le type "related"
            MimeMultipart multipart = new MimeMultipart("related");

            // Partie HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlString, "text/html");

            multipart.addBodyPart(htmlPart);

            // Fixer le contenu du message avec MimeMultipart
            message.setContent(multipart);

            // Envoi de l'email
            Transport.send(message);
            System.out.println("Email envoye avec succes !");
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();  // Imprime l'exception pour debugging
            throw new Exception("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
        }
    }
}