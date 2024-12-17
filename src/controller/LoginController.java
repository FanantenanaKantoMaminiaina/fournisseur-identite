package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/api/login")
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

      protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JsonParser parser = new JsonParser();
        JsonObject jsonRequest = parser.parse(requestBody.toString()).getAsJsonObject();
        StringBuilder cleValuer = new StringBuilder();
        for (String key : jsonRequest.keySet()) {
            JsonElement valueElement = jsonRequest.get(key);  
            String paramValue = valueElement.getAsString();  
            cleValuer.append("Clé : ").append(key)
                     .append(" | Valeur : ").append(paramValue)
                     .append("\n");
        }

        String jsonResponse = "{\"message\": \"Clé et valeur reçues avec succès\", \"data\": \"" + cleValuer.toString().replace("\n", "\\n") + "\"}";
        
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }
}
