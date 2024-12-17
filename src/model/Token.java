package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Token{
    int idToken;
    String token;
    Timestamp expirationToken;
    Utilisateur utilisateur;

    public Token() {
    }
    public int getIdToken() {
        return idToken;
    }
    public void setIdToken(int idToken) {
        this.idToken = idToken;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Timestamp getExpirationToken() {
        return expirationToken;
    }
    public void setExpirationToken(Timestamp expirationToken) {
        this.expirationToken = expirationToken;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    
    public void insertToken(Connection connection) throws SQLException {
        String query = "INSERT INTO token (token, expiration_token, id_utilisateur) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, this.token);
            preparedStatement.setTimestamp(2, this.expirationToken);
            preparedStatement.setInt(3, this.utilisateur.getIdUtilisateur());
            preparedStatement.executeUpdate();
        }
    }

    public static Token getTokenByUtilisateur(String email, Connection connection) throws SQLException {
        String query = "SELECT * FROM token WHERE id_utilisateur = (SELECT id_utilisateur FROM utilisateur WHERE email = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Token token = new Token();
                token.setIdToken(resultSet.getInt("id_token"));
                token.setToken(resultSet.getString("token"));
                token.setExpirationToken(resultSet.getTimestamp("expiration_token"));
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setIdUtilisateur(resultSet.getInt("id_utilisateur"));
                token.setUtilisateur(utilisateur);
                return token;
            }
        }
        return null; 
    }

    public void updateToken(Connection connection) throws SQLException {
        String query = "UPDATE token SET token = ?, expiration_token = ? WHERE id_token = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, this.token);
            preparedStatement.setTimestamp(2, this.expirationToken);
            preparedStatement.setInt(3, this.idToken);
            preparedStatement.executeUpdate();
        }
    }
}