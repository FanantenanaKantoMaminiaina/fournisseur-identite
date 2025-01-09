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
import util.ApiResponse;
import util.PropertiesLoader;

@WebServlet("/api/resetTentative")
public class ResetTentativeApiController extends HttpServlet {

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String email = request.getParameter("email");
        Connection connection = null;

        try {
            connection = Connexion.connect();

            if (email==null ) {
                response.getWriter().print(ApiResponse.error(400, "email est obligatoire", null));
                return;
            }

            Tentative.deleteTentativesByEmail(email, connection);

            Map<String, String> data = new HashMap<>();
            data.put("message", "Tentatives reinitialisees avec succes." );

            response.getWriter().print(ApiResponse.success(data));

            connection.commit();
        } catch (Exception ex) {
            response.getWriter().print(ApiResponse.error(500, "Erreur interne du serveur ", ex.getMessage()));
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