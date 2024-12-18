package model;

import java.sql.Timestamp;

public class Tentative{
    int idTentative;
    int nb;
    Timestamp dateReconnexion;
    Utilisateur utilisateur;
    
    public int getIdTentative() {
        return idTentative;
    }
    public void setIdTentative(int idTentative) {
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