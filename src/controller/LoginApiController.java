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
import model.Configuration;
import util.ApiResponse;
import util.Utilitaire;
import util.UtilitaireAuthentification;

@WebServlet("/api/login")
public class LoginApiController extends HttpServlet {

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

            if (!jsonRequest.has("email") || !jsonRequest.has("password")) {
                response.getWriter().print(ApiResponse.error(400, "Email and password are required", null));
                return;
            }

            String email = jsonRequest.get("email").getAsString();
            String password = jsonRequest.get("password").getAsString();

            Utilisateur utilisateur = Utilisateur.getUserByEmail(connection, email);

            if (utilisateur == null) {
                response.getWriter().print(ApiResponse.error(401, "User invalid. Verify your email", null));
                return;
            }

            if (!utilisateur.isValidPassword(password)) {
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

                response.getWriter().print(ApiResponse.error(401, "Invalid credentials. Password invalid", null));
                return;
            }else if(utilisateur.isValidPassword(password) && Tentative.getNbTentativeParEmail(email, connection) >= configuration.getLimiteTentative() ){
                response.getWriter().print(ApiResponse.error(401, "Trop de tentatives echouees. Compte temporairement bloque.", "/api/resetTentatives?email=" + email));
                return;
            }

            String pin = UtilitaireAuthentification.generatePin(6);
            int validationPin = utilisateur.insertPin(connection, pin, configuration.getDureeViePin());
            
            if(validationPin < 0){
                response.getWriter().print(ApiResponse.error(401, "Error to generate PIN",null));
                return;
            }

            Map<String, String> data = new HashMap<>();
            data.put("message", "Verify your email and authenticate with PIN");

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
