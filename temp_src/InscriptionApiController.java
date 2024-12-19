package controller;

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
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.SQLException;

import model.Utilisateur;
import util.ApiResponse;
import util.PropertiesLoader;

@WebServlet("/api/inscription")
public class InscriptionApiController extends HttpServlet {

    private String propertiesPath = "database.properties";
    private String emailExpediteur;
    private String passwordExpediteur;

    @Override
    public void init() throws ServletException {
        super.init();
        chargerConfigurations();
    }

    private void chargerConfigurations() {
        try {
            PropertiesLoader loader = new PropertiesLoader(this.propertiesPath);
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

            Utilisateur utilisateur = new Utilisateur(email ,mdp);
            utilisateur.inscription(connection, this.emailExpediteur , this.passwordExpediteur);

            Map<String, String> data = new HashMap<>();
            data.put("message", "Votre compte est en attente de validation, veuillez valider votre email!");

            response.getWriter().print(ApiResponse.success(data));
          
            connection.commit();
        } catch (Exception ex) {
            response.getWriter().print(ApiResponse.error(500, "Erreur interne de serveur , EMAIL = " +this.emailExpediteur + " , MDP = " +this.passwordExpediteur, ex.getMessage()));
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String validationToken = request.getParameter("validationToken");
        Connection connection = null;

        try {
            connection = Connexion.connect();

            if (validationToken==null ) {
                response.getWriter().print(ApiResponse.error(400, "validationToken est obligatoire", null));
                return;
            }
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.verifierEmail(connection ,validationToken);

            Map<String, String> data = new HashMap<>();
            data.put("message", "Votre compte a bien ete crée, veuillez vous connecter!");

            response.getWriter().print(ApiResponse.success(data));  
            connection.commit();
        } catch (Exception ex) {
            response.getWriter().print(ApiResponse.error(500, "Erreur interne de serveur , EMAIL = " +this.emailExpediteur + " , MDP = " +this.passwordExpediteur, ex.getMessage()));
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