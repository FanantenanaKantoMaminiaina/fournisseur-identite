
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Tentative{
    int idTentative;
    int nb;
    Timestamp dateReconnexion;
    Utilisateur utilisateur;

    public Tentative() { }

    public Tentative(int idTentative, int nb, Timestamp dateReconnexion, Utilisateur utilisateur) {
        this.idTentative = idTentative;
        this.dateReconnexion = dateReconnexion;
        this.nb = nb;
        this.utilisateur = utilisateur;
    }
    
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

    public int insererTentative(Connection connection) {
        String query = "INSERT INTO tentative (nb, date_reconnexion, id_utilisateur) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, this.nb); 
            stmt.setTimestamp(2, this.dateReconnexion);
            stmt.setInt(3, this.utilisateur.getIdUtilisateur()); 
            return stmt.executeUpdate(); 
        } catch (SQLException e) {
            e.printStackTrace();
            return 0; 
        }
    }
    public static void updateTentative(String email, int nbTentatives, Connection connection) {
        String query = "UPDATE tentative SET nb = ?, date_reconnexion = ? WHERE id_utilisateur = (SELECT id_utilisateur FROM utilisateur WHERE email = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, nbTentatives);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getNbTentativeParEmail(String email, Connection connection) {
        String query = """ 
                    SELECT t.nb FROM tentative t 
                    JOIN utilisateur u ON t.id_utilisateur = u.id_utilisateur 
                    WHERE u.email = ? """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("nb");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void deleteTentativesByEmail(String email, Connection connection) throws SQLException {
        String query = "DELETE FROM tentative WHERE id_utilisateur = (SELECT id_utilisateur FROM utilisateur WHERE email = ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
        }
    }
}
