package model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import connexion.Connexion;
import util.UtilitaireAuthentification;

public class Utilisateur{
    int idUtilisateur;
    String email;
    String mdp;

    public Utilisateur() {
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

    public int updateInfo(String[] colName, String[] value, String token, Connection connection) {
        if (colName == null || value == null || colName.length != value.length || colName.length == 0) {
            throw new IllegalArgumentException("Les tableaux colName et value doivent être non nuls, de même longueur et non vides.");
        }
        StringBuilder query = new StringBuilder("UPDATE utilisateur SET ");
        for (int i = 0; i < colName.length; i++) {
            if ("email".equalsIgnoreCase(colName[i])) {
                continue;
            }
            query.append(colName[i]).append(" = ?, ");
        }
        query.setLength(query.length() - 2); 
        query.append(" WHERE id_utilisateur = (SELECT id_utilisateur FROM token WHERE token = ?)");
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int index = 1;
            for (int i = 0; i < colName.length; i++) {
                if ("email".equalsIgnoreCase(colName[i])) {
                    continue; 
                }
                stmt.setString(index++, value[i]);
            }
            stmt.setString(index, token); 
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0; 
        }
    }

     public boolean verifierMdp(String email, String mdpLe, Connection connection) {
        String query = "SELECT mdp FROM utilisateur WHERE email = ?";
        mdpLe=UtilitaireAuthentification.hashPassword(mdpLe);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String mdpHash = rs.getString("mdp");
                if (mdpHash != null && mdpHash.equals(mdpLe)) {
                    return true; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
    }
    public boolean verifierCodePin(String email, String codePin, Connection connection) {
        String query = "SELECT code_pin, expiration_pin FROM authentification WHERE id_utilisateur = (SELECT id_utilisateur FROM utilisateur WHERE email = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedCodePin = rs.getString("code_pin");
                Timestamp expirationPin = rs.getTimestamp("expiration_pin");
                if (storedCodePin != null && storedCodePin.equals(codePin)) {
                    if (expirationPin != null && expirationPin.after(new Timestamp(System.currentTimeMillis()))) {
                        return true; 
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
    }

    public Utilisateur verifierEmailExist(String email, Connection connection) {
        String query = "SELECT id_utilisateur, email, mdp FROM utilisateur WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setIdUtilisateur(rs.getInt("id_utilisateur"));
                utilisateur.setEmail(rs.getString("email"));
                utilisateur.setMdp(rs.getString("mdp"));
                return utilisateur; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
}

