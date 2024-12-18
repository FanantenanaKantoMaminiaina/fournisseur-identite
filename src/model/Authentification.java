package model;

import java.sql.Timestamp;

public class Authentification{
    int idAuthentification;
    int codePin;
    Timestamp expirationPin;
    Utilisateur utilisateur;

    public int getIdAuthentification() {
        return idAuthentification;
    }
    public void setIdAuthentification(int idAuthentification) {
        this.idAuthentification = idAuthentification;
    }
    public int getCodePin() {
        return codePin;
    }
    public void setCodePin(int codePin) {
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