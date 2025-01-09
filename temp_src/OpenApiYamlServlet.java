package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

@WebServlet("/openapi.yaml")
public class OpenApiYamlServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/x-yaml");

        // Charger le fichier openapi.yaml depuis WEB-INF
        InputStream yamlStream = getServletContext().getResourceAsStream("/WEB-INF/openapi.yaml");
        if (yamlStream == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Fichier YAML non trouvé.");
            return;
        }

        // Envoyer le contenu du fichier YAML en réponse
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = yamlStream.read(buffer)) != -1) {
            resp.getOutputStream().write(buffer, 0, bytesRead);
        }
        yamlStream.close();
    }
}
