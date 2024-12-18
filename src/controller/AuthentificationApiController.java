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
import model.Configuration;
import util.ApiResponse;
import util.Utilitaire;
import util.UtilitaireAuthentification;

@WebServlet("/api/authentification")
public class AuthentificationApiController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        Connection connection = null;

        try {
            connection = Connexion.connect();

            Configuration configuration = Configuration.getConfiguration(connection);

            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
            }

            JsonObject jsonRequest = new Gson().fromJson(jsonBuffer.toString(), JsonObject.class);

            if (!jsonRequest.has("email") || !jsonRequest.has("pin")) {
                response.getWriter().print(ApiResponse.error(400, "Email and pin are required", null));
                return;
            }

            String email = jsonRequest.get("email").getAsString();
            String pin = jsonRequest.get("pin").getAsString();

            Utilisateur utilisateur = Utilisateur.getUserByEmail(connection, email);

            if (utilisateur == null) {
                response.getWriter().print(ApiResponse.error(401, "User invalid. Verify your email", null));
                return;
            }

            Authentification authentification = utilisateur.getLastAuthentification(connection);

            if (authentification == null) {
                response.getWriter().print(ApiResponse.error(401, "Authentification invalid", null));
                return;
            }else if (!authentification.getCodePin().equals(pin)) {
                int nbTentative = Tentative.getNbTentativeParEmail(email, connection);
                if (nbTentative >= configuration.getLimiteTentative() ) {
                    response.getWriter().print(ApiResponse.error(401, "Trop de tentatives echouees. Compte temporairement bloque.", "/api/resetTentatives?email=" + email));
                    return;
                }

                if (nbTentative == 0) {
                    Tentative tentative = new Tentative(1, 1, Utilitaire.getNow(), utilisateur);
                    tentative.insererTentative(connection);
                } else {
                    Tentative.updateTentative(email, nbTentative + 1, connection);
                }

                connection.commit();
                
                response.getWriter().print(ApiResponse.error(401, "Code PIN invalid", null));
                return;
            }else if (authentification.getExpirationPin().before(Utilitaire.getNow())) {
                response.getWriter().print(ApiResponse.error(401, "Code PIN expired", null));
                return;
            }

            String token = UtilitaireAuthentification.generateRandomToken();
            Timestamp expirationToken = Utilitaire.getNow();
            expirationToken = Utilitaire.addSeconds(expirationToken, configuration.getDureeVieToken());                        
            utilisateur.reinitialiseTentative(connection);

            int validationToken = utilisateur.addToken(connection, token, expirationToken);
            
            if(validationToken < 0){
                response.getWriter().print(ApiResponse.error(401, "Error to generate Token",null));
                return;
            }

            Map<String, String> data = new HashMap<>();
            data.put("token", token );

            response.getWriter().print(ApiResponse.success(data));
            
            connection.commit();
        } catch (Exception ex) {
            response.getWriter().print(ApiResponse.error(500, "Internal Server Error", ex.getMessage()));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    response.getWriter().print(ApiResponse.error(500, "Error connection", e.getMessage()));
                }
            }
        }
    }
}
