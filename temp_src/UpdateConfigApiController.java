package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import model.Configuration;
import util.ApiResponse;

@WebServlet("/api/updateConfig")
public class UpdateConfigApiController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        Connection connection = null;

        try {
            // {
            //     "duree_vie_token": 30,
            //     "duree_vie_pin": 15,
            //     "limite_tentative": 5
            // }

            connection = Connexion.connect();

            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
            }

            JsonObject jsonRequest = new Gson().fromJson(jsonBuffer.toString(), JsonObject.class);

            if (!jsonRequest.has("duree_vie_token") || !jsonRequest.has("duree_vie_pin") || !jsonRequest.has("limite_tentative")) {
                response.getWriter().print(ApiResponse.error(400, "Les parametres duree_vie_token, duree_vie_pin et limite_tentative sont requis.", null));
                return;
            }

            int dureeVieToken = jsonRequest.get("duree_vie_token").getAsInt();
            int dureeViePin = jsonRequest.get("duree_vie_pin").getAsInt();
            int limiteTentative = jsonRequest.get("limite_tentative").getAsInt();

            Configuration config = new Configuration( 1 ,dureeVieToken ,dureeViePin ,limiteTentative);

            int rowsUpdated = config.update(connection);

            if (rowsUpdated > 0) {
                Map<String, String> data = new HashMap<>();
                data.put("message", "Configuration mise a jour avec succes.");
                response.getWriter().print(ApiResponse.success(data));
            } else {
                response.getWriter().print(ApiResponse.error(500, "Aucune ligne mise a jour.", null));
            }

            connection.commit();
        } catch (IOException | SQLException ex) {
            response.getWriter().print(ApiResponse.error(500, "Erreur interne du serveur", ex.getMessage()));
        } finally {
            // Fermer la connexion
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    response.getWriter().print(ApiResponse.error(500, "Erreur lors de la fermeture de la connexion", e.getMessage()));
                }
            }
        }
    }
}
