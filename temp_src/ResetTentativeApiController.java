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
import model.Configuration;
import util.ApiResponse;

@WebServlet("/api/resetTentative")
public class ResetTentativeApiController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            if (!jsonRequest.has("email")) {
                response.getWriter().print(ApiResponse.error(400, "Email is required", null));
                return;
            }

            String email = jsonRequest.get("email").getAsString();

            Tentative.deleteTentativesByEmail(email, connection);

            Map<String, String> data = new HashMap<>();
            data.put("message", "Tentatives reinitialisees avec succes." );

            response.getWriter().print(ApiResponse.success(data));

            connection.commit();
        } catch (IOException | SQLException ex) {
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