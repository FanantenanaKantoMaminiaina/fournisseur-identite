package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import connexion.Connexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import model.Authentification;
import model.Configuration;
import model.Tentative;
import model.Token;
import model.Utilisateur;
import util.ApiResponse;
import util.Utilitaire;
import util.UtilitaireAuthentification;

@WebServlet("/api/updateCompte")
public class UpdateCompteApiController extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        if (!UtilitaireAuthentification.verifyHeader(request, response, "Authorization")) {
            return; 
        }
        Connection connection = null;
        try {
            connection = Connexion.connect();
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            String data="";
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
            }
            List<String> keys=new ArrayList<>();
            List<String> values=new ArrayList<>();

            JsonObject jsonRequest = new Gson().fromJson(jsonBuffer.toString(), JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : jsonRequest.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().getAsString();
                keys.add(key);
                values.add(value); 
                data+= key+" ";
            }
            String[] keyArray=keys.toArray(new String[0]);
            String[] valueArray=values.toArray(new String[0]);

            Token token=Token.isValidToken(UtilitaireAuthentification.extractToken(request,"Authorization"), connection);
            
            if(token==null){
                response.getWriter().write(ApiResponse.error(500, "token est invalide", null));
                return;
            }
            Utilisateur.updateInfo(keyArray,valueArray,token.getToken(),connection);
            connection.commit();
            response.getWriter().write(ApiResponse.success(data+" mis a jour avec succes!"));

        } catch (Exception ex) {
            response.getWriter().print(ApiResponse.error(500, "Erreur interne du Server", ex.getMessage()));
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