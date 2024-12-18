
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import util.Utilitaire;
import util.UtilitaireAuthentification;

public class Utilisateur{
    public Utilisateur() {
    }

    int idUtilisateur;
    String email;
    String mdp;

    public Utilisateur(int idUtilisateur ,String email, String mdp) {
        this.setEmail(email);
        this.setIdUtilisateur(idUtilisateur);
        this.setMdp(mdp);
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }
    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMdp() {
        return mdp;
    }
    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public static Utilisateur getUserByEmail(Connection connection,String email)throws Exception{
        Utilisateur utilisateur = null;
        String query = "SELECT * FROM utilisateur WHERE email=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString( 1 , email );
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    utilisateur = new Utilisateur(
                        resultSet.getInt("id_utilisateur"), 
                        resultSet.getString("email"), 
                        resultSet.getString("mdp")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error while fetching Utilisateur", e);
        }
        return utilisateur;
    }

    public boolean isValidPassword(String password){
        return UtilitaireAuthentification.isPasswordValid(this.getMdp(),password);
    }

    public int insertPin(Connection connection, String pin ,int delaisPin)throws Exception {
        String sql = "INSERT INTO authentification (code_pin, expiration_pin, id_utilisateur) VALUES (?, ?, ?)";
        int generatedId = -1;

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, pin);              
            statement.setTimestamp(2, Utilitaire.addSeconds(Utilitaire.getNow(),delaisPin));
            statement.setInt(3, this.getIdUtilisateur());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }

        return generatedId;
    }

    public Authentification getLastAuthentification(Connection connection)throws Exception {
        
        Authentification authentification = null;

        String query = "SELECT * FROM authentification WHERE id_utilisateur = ? ORDER BY expiration_pin DESC LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt( 1 , this.getIdUtilisateur());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    authentification = new Authentification(
                        resultSet.getInt("id_authentification"), 
                        resultSet.getString("code_pin"), 
                        resultSet.getTimestamp("expiration_pin"), 
                        this
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error while fetching Authentification", e);
        }
        return authentification;
    }

    public int addToken(Connection connection, String token,Timestamp expirationToken)throws Exception {
        String sql = "INSERT INTO token (token, expiration_token, id_utilisateur) VALUES (?, ?, ?)";
        int generatedId = -1;

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, token);              
            statement.setTimestamp(2, expirationToken);
            statement.setInt(3, this.getIdUtilisateur());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }

        return generatedId;
    }

    public int reinitialiseTentative(Connection connection)throws Exception{
        int affectedRows = -1;
        String sql = "DELETE FROM tentative WHERE id_utilisateur = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.getIdUtilisateur());

            affectedRows = statement.executeUpdate();
            
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }

        return affectedRows;
    }

    public static int updateInfo(String[] colName, String[] value, String token, Connection connection) throws Exception {
        if (colName == null || value == null || colName.length != value.length || colName.length == 0) {
            throw new IllegalArgumentException("Les tableaux colName et value doivent être non nuls, de même longueur et non vides.");
        }
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Le token doit être non nul et non vide.");
        }
    
        StringBuilder query = new StringBuilder("UPDATE utilisateur SET ");
        int columnsAdded = 0; 
        for (int i = 0; i < colName.length; i++) {
            if ("email".equalsIgnoreCase(colName[i])) {
                continue; 
            }
            query.append(colName[i]).append(" = ?, ");
            columnsAdded++;
        }
    
        if (columnsAdded == 0) {
            throw new IllegalArgumentException("Aucune colonne valide à mettre à jour. Vérifiez vos paramètres.");
        }
    
        query.setLength(query.length() - 2);
        query.append(" WHERE id_utilisateur = (SELECT id_utilisateur FROM token WHERE token = ?)");
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int index = 1;
            for (int i = 0; i < colName.length; i++) {
                if ("email".equalsIgnoreCase(colName[i])) {
                    continue; 
                }
                String valueToSet = value[i];
                if ("mdp".equalsIgnoreCase(colName[i])) {
                    valueToSet = UtilitaireAuthentification.hashPassword(value[i]);
                }
                stmt.setString(index++, valueToSet); 
            }
            stmt.setString(index, token); 
    
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'exécution de la requête updateInfo: " + e.getMessage(), e);
        }
    }

    public static Utilisateur getUserByToken(Connection connection, String token) throws Exception {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Le token doit être non nul et non vide.");
        }
    
        Utilisateur utilisateur = null;
        String query = "SELECT u.id_utilisateur, u.email, u.mdp " +
                       "FROM utilisateur u " +
                       "INNER JOIN token t ON u.id_utilisateur = t.id_utilisateur " +
                       "WHERE t.token = ? AND t.expiration_token > ?";
    
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, token);
            statement.setTimestamp(2, Utilitaire.getNow());
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    utilisateur = new Utilisateur(
                        resultSet.getInt("id_utilisateur"),
                        resultSet.getString("email"),
                        resultSet.getString("mdp")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération de l'utilisateur par token: " + e.getMessage(), e);
        }
    
        return utilisateur;
    }

    public static Token getLastTokenByIdUtilisateur(int idUtilisateur, Connection connection) throws Exception {
        if (idUtilisateur <= 0) {
            throw new IllegalArgumentException("L'idUtilisateur doit être supérieur à 0.");
        }
        String query = "SELECT id_token, token, expiration_token, id_utilisateur FROM token WHERE id_utilisateur = ? ORDER BY expiration_token DESC LIMIT 1";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUtilisateur); 
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Token token = new Token();
                    token.setToken(resultSet.getString("token"));
                    token.setExpirationToken(resultSet.getTimestamp("expiration_token"));
                    token.setIdToken(resultSet.getInt("id_token"));
                    Utilisateur utilisateur =new Utilisateur();
                    utilisateur.setIdUtilisateur(idUtilisateur);
                    token.setUtilisateur(utilisateur);
    
                    return token; 
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération du dernier token: " + e.getMessage(), e);
        }
        
        return null;
    }
}    
