package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Authentification;
import model.Tentative;
import model.Utilisateur;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import connexion.Connexion;
import model.Authentification;
import util.UtilitaireAuthentification;

@WebServlet("/api/auth")
public class AuthentificationController extends HttpServlet {

    private static final int MAX_TENTATIVES = 3;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JsonObject jsonResponse = new JsonObject();

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        JsonParser parser = new JsonParser();
        JsonObject jsonRequest = parser.parse(requestBody.toString()).getAsJsonObject();
        String email = jsonRequest.get("email").getAsString();
        String mdp = jsonRequest.get("mdp").getAsString();

        Connection connection = null;
        try {
            connection = Connexion.connect();
            Utilisateur utilisateur = new Utilisateur();
            utilisateur = utilisateur.verifierEmailExist(email, connection);

            if (utilisateur == null) {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("error", "Email introuvable.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            if (!utilisateur.verifierMdp(email, mdp, connection)) {
                Tentative tentative = new Tentative();
                int nbTentative = tentative.getNbTentativeParEmail(email, connection);
                if (nbTentative >= MAX_TENTATIVES) {
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("error", "Trop de tentatives échouées. Compte temporairement bloqué.");
                    jsonResponse.addProperty("resetLink", "/api/resetTentatives?email=" + email);
                    response.getWriter().write(jsonResponse.toString());
                    return;
                }

                if (nbTentative == 0) {
                    tentative.setNb(1);
                    tentative.setDateReconnexion(new Timestamp(System.currentTimeMillis()));
                    tentative.setUtilisateur(utilisateur);
                    tentative.insererTentative(connection);
                } else {
                    tentative.updateTentative(email, nbTentative + 1, connection);
                }
                connection.commit();
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("error", "Mot de passe incorrect.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            Authentification authentification = Authentification.getByUtilisateur(utilisateur.getIdUtilisateur(), connection);
            String pinCode;
            Timestamp expirationTime;
            if (authentification == null) {
                authentification = new Authentification();
                pinCode = UtilitaireAuthentification.generatePin(6);
                expirationTime = new Timestamp(System.currentTimeMillis() + 50 * 1000); 
                authentification.setCodePin(pinCode);
                authentification.setExpirationPin(expirationTime);
                authentification.setUtilisateur(utilisateur);
                authentification.insertCodePin(connection);
            } else {
                pinCode = UtilitaireAuthentification.generatePin(6);
                expirationTime = new Timestamp(System.currentTimeMillis() + 50 * 1000); 
                authentification.setCodePin(pinCode);
                authentification.setExpirationPin(expirationTime);
                authentification.setUtilisateur(utilisateur);
                authentification.updateCodePin(connection);
            }
            
            connection.commit();
            
            jsonResponse.addProperty("status", "success");
            JsonObject data = new JsonObject();
            data.addProperty("idUtilisateur", utilisateur.getIdUtilisateur());
            data.addProperty("email", utilisateur.getEmail());
            data.addProperty("codePin", pinCode);  
            jsonResponse.add("data", data);

            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("error", e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



