package controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.google.gson.JsonObject;

import connexion.Connexion;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Tentative;

@WebServlet("/api/resetTentatives")
public class ResetTentativesController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JsonObject jsonResponse = new JsonObject();
        String email = request.getParameter("email");

        if (email == null || email.isEmpty()) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("error", "Email manquant.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        Connection connection = null;
        try {
            connection = Connexion.connect();
            Tentative tentative = new Tentative();
            tentative.deleteTentativesByEmail(email, connection);

            connection.commit();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("data", "Tentatives reinitialisees avec succes.");
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
