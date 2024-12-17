package model;

import java.sql.Timestamp;

public class Tentative{
    String idTentative;
    int nb;
    Timestamp dateReconnexion;
    Utilisateur utilisateur;
    
    public String getIdTentative() {
        return idTentative;
    }
    public void setIdTentative(String idTentative) {
        this.idTentative = idTentative;
    }
    public int getNb() {
        return nb;
    }
    public void setNb(int nb) {
        this.nb = nb;
    }
    public Timestamp getDateReconnexion() {
        return dateReconnexion;
    }
    public void setDateReconnexion(Timestamp dateReconnexion) {
        this.dateReconnexion = dateReconnexion;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}