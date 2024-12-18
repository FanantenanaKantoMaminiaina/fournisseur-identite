package util;

import java.sql.Connection;

import connexion.Connexion;
import model.Utilisateur;

public class Test {
    public static void main(String[] args) {
        // UtilitaireAuthentification ut = new UtilitaireAuthentification();
        // String hashPassword = UtilitaireAuthentification.hashPassword("mdp");
        // System.out.println(hashPassword);
        // System.out.println(ut.isPasswordValid("mdp", hashPassword));
        // String randomtoken = UtilitaireAuthentification.generateRandomToken();
        // System.out.println(randomtoken);
        // System.out.println(UtilitaireAuthentification.hashPassword(randomtoken));

        // System.out.println(Utilisateur.preparerEmailValidation("12345667token"));
        try(Connection connection = Connexion.connect()) {
            Utilisateur util = new Utilisateur();
            util.inscription("razafindralambo.nambinina@gmail.com", "monMdp");
            // util.verifierEmail("48d1cd59b6938dac8eac25a1e56f695d3fb09abd04feae3381ac69b30eb0aa93");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
