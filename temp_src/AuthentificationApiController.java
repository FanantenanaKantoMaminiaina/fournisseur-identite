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
import java.sql.Timestamp;

import model.Utilisateur;
import model.Authentification;
import model.Tentative;
import util.ApiResponse;
import util.Utilitaire;
import util.UtilitaireAuthentification;
import util.PropertiesLoader;
import util.UtilitaireEnvoieEmail;

@WebServlet("/api/authentification")
public class AuthentificationApiController extends HttpServlet {

    private String propertiesPath = "database.properties";
    private int duree_vie_token;
    private int limite_tentative;
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
            this.duree_vie_token = Integer.parseInt(loader.getProperty("duree_vie_token"));
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

            // Lire et parser le JSON de la requête
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
            }

            JsonObject jsonRequest = new Gson().fromJson(jsonBuffer.toString(), JsonObject.class);

            // Validation des champs obligatoires
            if (!jsonRequest.has("email") || !jsonRequest.has("pin")) {
                response.getWriter().print(ApiResponse.error(400, "email et pin sont obligatoires", null));
                return;
            }

            String email = jsonRequest.get("email").getAsString();
            String pin = jsonRequest.get("pin").getAsString();

            // Vérification de l'utilisateur
            Utilisateur utilisateur = Utilisateur.getUserByEmail(connection, email);
            if (utilisateur == null) {
                response.getWriter().print(ApiResponse.error(401, "Aucun utilisateur, verifier votre email", null));
                return;
            }

            // Vérification de l'authentification
            Authentification authentification = utilisateur.getLastAuthentification(connection);
            if (authentification == null) {
                response.getWriter().print(ApiResponse.error(401, "Authentification est invalide", null));
                return;
            } else if (!authentification.getCodePin().equals(pin)) {

                int nbTentative = Tentative.getNbTentativeParEmail(email, connection);
                if (nbTentative >= this.limite_tentative) {
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
                response.getWriter().print(ApiResponse.error(401, "Code PIN est invalide", null));
                return;
            } else if (authentification.getExpirationPin().before(Utilitaire.getNow())) {
                response.getWriter().print(ApiResponse.error(401, "Code PIN expiree", null));
                return;
            }

            // Génération du token
            String token = UtilitaireAuthentification.generateRandomToken();
            Timestamp expirationToken = Utilitaire.addSeconds(Utilitaire.getNow(), this.duree_vie_token);
            utilisateur.reinitialiseTentative(connection);

            int validationToken = utilisateur.addToken(connection, token, expirationToken);
            if (validationToken < 0) {
                response.getWriter().print(ApiResponse.error(401, "Erreur de generation de Token", null));
                return;
            }

            Map<String, String> data = new HashMap<>();
            data.put("token", token);

            response.getWriter().print(ApiResponse.success(data));
            connection.commit();

        } catch (Exception ex) {
            response.getWriter().print(ApiResponse.error(500, "Erreur interne du serveur", ex.getMessage()));
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
