package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Authentification{
    int idAuthentification;
    String codePin;
    Timestamp expirationPin;
    Utilisateur utilisateur;

    public Authentification() {
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
    public Authentification verifierCodePin(String email, String codePin, Connection connection) {
        String query = "SELECT code_pin, expiration_pin, id_utilisateur FROM authentification WHERE id_utilisateur = (SELECT id_utilisateur FROM utilisateur WHERE email = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedCodePin = rs.getString("code_pin");
                Timestamp expirationPin = rs.getTimestamp("expiration_pin");

                if (storedCodePin != null && storedCodePin.equals(codePin)) {
                    if (expirationPin != null && expirationPin.after(new Timestamp(System.currentTimeMillis()))) {
                        Utilisateur utilisateur = new Utilisateur();
                        utilisateur = utilisateur.verifierEmailExist(email,connection);
                        Authentification authentification = new Authentification();
                        authentification.setCodePin(storedCodePin);
                        authentification.setExpirationPin(expirationPin);
                        authentification.setUtilisateur(utilisateur);
                        return authentification;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    public void insertCodePin(Connection connection) {
        String query = "INSERT INTO authentification (code_pin, expiration_pin, id_utilisateur) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, codePin);
            stmt.setTimestamp(2, expirationPin);
            stmt.setInt(3, utilisateur.getIdUtilisateur());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Authentification getByUtilisateur(int idUtilisateur, Connection connection) {
        String query = "SELECT code_pin, expiration_pin FROM authentification WHERE id_utilisateur = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Authentification authentification = new Authentification();
                authentification.setCodePin(rs.getString("code_pin"));
                authentification.setExpirationPin(rs.getTimestamp("expiration_pin"));
                return authentification;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateCodePin(Connection connection) {
        String query = "UPDATE authentification SET code_pin = ?, expiration_pin = ? WHERE id_utilisateur = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, this.codePin);
            stmt.setTimestamp(2, this.expirationPin);
            stmt.setInt(3, this.utilisateur.getIdUtilisateur());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}