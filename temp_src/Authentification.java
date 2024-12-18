package model;

import java.sql.Timestamp;

public class Authentification{
    int idAuthentification;
    String codePin;
    Timestamp expirationPin;
    Utilisateur utilisateur;

    public Authentification(int idAuthentification, String codePin, Timestamp expirationPin, Utilisateur utilisateur) {
        this.idAuthentification = idAuthentification;
        this.codePin = codePin;
        this.expirationPin = expirationPin;
        this.utilisateur = utilisateur;
    }

    public int getIdAuthentification() {
        return idAuthentification;
    }
    public void setIdAuthentification(int idAuthentification) {
        this.idAuthentification = idAuthentification;
    }
    public String getCodePin() {
        return codePin;
    }
    public void setCodePin(String codePin) {
        this.codePin = codePin;
    }
    public Timestamp getExpirationPin() {
        return expirationPin;
    }
    public void setExpirationPin(Timestamp expirationPin) {
        this.expirationPin = expirationPin;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

}