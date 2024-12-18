package connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {
    public static Connection connect() throws Exception {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/fournisseur_identite";
            String username = "postgres";
            String password = "ceCillia250";
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
        return connection;
    }
}