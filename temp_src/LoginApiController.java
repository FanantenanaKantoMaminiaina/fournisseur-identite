package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import connexion.Connexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;

import model.Tentative;
import model.Utilisateur;
import util.ApiResponse;
import util.Utilitaire;
import util.UtilitaireAuthentification;
import util.UtilitaireEnvoieEmail;
import util.PropertiesLoader;

@WebServlet("/api/login")
public class LoginApiController extends HttpServlet {
    
    private int limite_tentative;
    private int duree_vie_pin;
    private String emailExpediteur;
    private String passwordExpediteur;

    @Override
    public void init() throws ServletException {
        super.init();
        chargerConfigurations();
    }

    private void chargerConfigurations() {
        try {
            PropertiesLoader loader = new PropertiesLoader("database.properties");
            this.limite_tentative = Integer.parseInt(loader.getProperty("limite_tentative"));
            this.duree_vie_pin = Integer.parseInt(loader.getProperty("duree_vie_pin"));
            this.emailExpediteur = loader.getProperty("emailExpediteur");
            this.passwordExpediteur = loader.getProperty("passwordExpediteur");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des configurations : " + e.getMessage(), e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        Connection connection = null;

        try {
            connection = Connexion.connect();

            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
            }

            JsonObject jsonRequest = new Gson().fromJson(jsonBuffer.toString(), JsonObject.class);

            if (!jsonRequest.has("email") || !jsonRequest.has("mdp")) {
                response.getWriter().print(ApiResponse.error(400, "email et mdp sont obligatoires", null));
                return;
            }

            String email = jsonRequest.get("email").getAsString();
            String mdp = jsonRequest.get("mdp").getAsString();

            Utilisateur utilisateur = Utilisateur.getUserByEmail(connection, email);

            if (utilisateur == null) {
                response.getWriter().print(ApiResponse.error(401, "Aucun utilisateur,verifier votre email", null));
                return;
            }

            if (!utilisateur.isValidPassword(mdp)) {
                int nbTentative = Tentative.getNbTentativeParEmail(email, connection);
                if (nbTentative >= this.limite_tentative ) {
                    boolean envoyeEmail = UtilitaireEnvoieEmail.envoyerEmail(
                        this.emailExpediteur,
                        this.passwordExpediteur,
                        utilisateur.getEmail(),
                        UtilitaireAuthentification.getHtmlResetTentative("http://localhost:8080/fournisseur-identite/api/resetTentative?email=" + email)
                    );
                    response.getWriter().print(ApiResponse.error(401, "Trop de tentatives echouees. Compte temporairement bloque.", "http://localhost:8080/fournisseur-identite/api/resetTentative?email=" + email));
                    return;
                }

                if (nbTentative == 0) {
                    Tentative tentative = new Tentative(1, 1, Utilitaire.getNow(), utilisateur);
                    tentative.insererTentative(connection);
                } else {
                    Tentative.updateTentative(email, nbTentative + 1, connection);
                }

                connection.commit();

                response.getWriter().print(ApiResponse.error(401, "mdp est invalide", null));
                return;
            }else if(utilisateur.isValidPassword(mdp) && Tentative.getNbTentativeParEmail(email, connection) >= this.limite_tentative ){
                boolean envoyeEmail = UtilitaireEnvoieEmail.envoyerEmail(
                    this.emailExpediteur,
                    this.passwordExpediteur,
                    utilisateur.getEmail(),
                    UtilitaireAuthentification.getHtmlResetTentative("http://localhost:8080/fournisseur-identite/api/resetTentative?email=" + email)
                );
                response.getWriter().print(ApiResponse.error(401, "Trop de tentatives echouees. Compte temporairement bloque.", "http://localhost:8080/fournisseur-identite/api/resetTentative?email=" + email));
                return;
            }

            String pin = UtilitaireAuthentification.generatePin(6);
            int validationPin = utilisateur.insertPin(connection, pin, this.duree_vie_pin);

            boolean envoyePin = UtilitaireEnvoieEmail.envoyerEmail(
                this.emailExpediteur,
                this.passwordExpediteur,
                utilisateur.getEmail(),
                UtilitaireAuthentification.getHtmlPin(pin, this.duree_vie_pin)
            );

            if(validationPin < 0 && !envoyePin){
                response.getWriter().print(ApiResponse.error(401, "Errur pour la generation de pin",null));
                return;
            }

            Map<String, String> data = new HashMap<>();
            data.put("message", "Login effectue,verifier votre email pour s'authentifier avec pin");

            response.getWriter().print(ApiResponse.success(data));
            
            connection.commit();
        } catch (Exception ex) {
            response.getWriter().print(ApiResponse.error(500, "Erreur interne du serveur , EMAIL = " +this.emailExpediteur + " ,MDP = " +this.passwordExpediteur, ex.getMessage()));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    response.getWriter().print(ApiResponse.error(500, "Erreur de connection", e.getMessage()));
                }
            }
        }
    }
}
