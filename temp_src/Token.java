package model;

import java.sql.Timestamp;

public class Token{
    String idToken;
    String token;
    Timestamp expirationToken;
    Utilisateur utilisateur;

    public String getIdToken() {
        return idToken;
    }
    public void setIdToken(String idToken) {
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
}