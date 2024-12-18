package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;

import model.Tentative;
import model.Utilisateur;
import util.ApiResponse;
import util.Utilitaire;
import util.UtilitaireAuthentification;

@WebServlet("/api/inscription")
public class InscriptionApiController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String email = request.getParameter("email");
        String mdp =request.getParameter("mdp");
        try {
            if (email==null || mdp==null) {
                response.getWriter().print(ApiResponse.error(400, "Email and password are required", null));
                return;
            }
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.inscription(email, mdp);

            Map<String, String> data = new HashMap<>();
            data.put("message", "Votre compte est en attente de validation, veuillez valider votre email!");

            response.getWriter().print(ApiResponse.success(data));
            // response.getWriter().print("hello");
          
        } catch (Exception ex) {
            response.getWriter().print(ApiResponse.error(500, "Internal Server Error", ex.getMessage()));
        }
      
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String validationToken = request.getParameter("validationToken");
        try {
            if (validationToken==null ) {
                response.getWriter().print(ApiResponse.error(400, "Token is required", null));
                return;
            }
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.verifierEmail(validationToken);

            Map<String, String> data = new HashMap<>();
            data.put("message", "Votre compte a bien ete crée, veuillez vous connecter!");

            response.getWriter().print(ApiResponse.success(data));
            // response.getWriter().print("hello");
          
        } catch (Exception ex) {
            response.getWriter().print(ApiResponse.error(500, "Internal Server Error", ex.getMessage()));
        }
      
    }
}