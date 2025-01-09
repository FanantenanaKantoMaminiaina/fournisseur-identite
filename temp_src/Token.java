package model;

import java.sql.Connection;
import java.sql.Timestamp;
import util.Utilitaire;

public class Token{
    int idToken;
    String token;
    Timestamp expirationToken;
    Utilisateur utilisateur;

    public Token(){ }

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

    public static Token isValidToken(String token, Connection connection) throws Exception {
        try {
            Utilisateur utilisateur = Utilisateur.getUserByToken(connection, token);
            if (utilisateur != null) {
                Token lastToken = Utilisateur.getLastTokenByIdUtilisateur(utilisateur.getIdUtilisateur(), connection);
                if (lastToken != null && lastToken.getToken().equals(token) && lastToken.getExpirationToken().after(Utilitaire.getNow())) {
                    return lastToken; 
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur inattendue lors de la validation du token: " + e.getMessage(), e);
        }
        return null;
    }
}