package main;

import model.*;;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Main {
    public static void main(String[] args) {
        // Remplacez par vos informations de connexion
        String url = "jdbc:mysql://localhost:3306/nom_de_votre_base_de_donnees";
        String user = "votre_utilisateur";
        String password = "votre_mot_de_passe";
        Utilisateur utilisateur=new Utilisateur();
        utilisateur.setIdUtilisateur(1);

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Tentative tentative = new Tentative();
            tentative.setNb(1);
            tentative.setDateReconnexion(new Timestamp(System.currentTimeMillis()));
            tentative.setUtilisateur(utilisateur);
            int result = tentative.insererTentative(connection);

            if (result > 0) {
                System.out.println("Tentative insérée avec succès.");
            } else {
                System.out.println("Échec de l'insertion de la tentative.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
