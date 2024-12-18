package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import connexion.Connexion;
import util.UtilitaireAuthentification;
import util.UtilitaireEnvoieEmail;

public class Utilisateur{
    int idUtilisateur;
    String email;
    String mdp;

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

    // insertion 
    private void inserer(String email, String mdp,Connection connection)throws SQLException{
        String sql = "INSERT INTO utilisateur (email, mdp) " +
                     "VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, mdp);
            preparedStatement.executeUpdate();
            System.out.println("Insertion réussie dans utilisateur.");
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'insertion dans utilisateur_temp : " + e.getMessage());
        }
    }

    // insertion temporaire dans utilisateur_temp
    public String insererUtilisateurTemporaire(String email, String mdp, Connection connection) throws SQLException {
        String sql = "INSERT INTO utilisateur_temp (email, mdp, validation_token, expiration_date) " +
                     "VALUES (?, ?, ?, NOW() + INTERVAL '15 minutes')";
    
        String hashMdp = UtilitaireAuthentification.hashPassword(mdp);
        String validationToken = UtilitaireAuthentification.generateRandomToken();
        String hashToken = UtilitaireAuthentification.hashPassword(validationToken);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, hashMdp);
            preparedStatement.setString(3, hashToken);
            preparedStatement.executeUpdate();
            // System.out.println("Insertion réussie dans utilisateur_temp.");
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'insertion dans utilisateur_temp : " + e.getMessage());
        }catch(Exception ex){
            throw ex;
        }
        return validationToken;
    }

    private static String preparerEmailValidation(String validation_token) {
        String htmlEmplacement = "N:\\ITU\\S5\\WEB\\ProjetCloud\\Examen\\fournisseur-identite\\assets\\validationCompte.html";
        try {
            // Lire le contenu du fichier HTML
            String htmlContent = new String(Files.readAllBytes(Paths.get(htmlEmplacement)));

            // Remplacer le token dans l'URL
            String url = "https://votre-site.com/valider?token=" + validation_token;
            htmlContent = htmlContent.replace("https://votre-site.com/valider?token=123456", url);

            return htmlContent;
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur lors de la lecture du fichier HTML.";
        }
    }

    // envoyer un email avec le lien de validation
    private static void envoyerEmailValidation(String email,String validation_token)throws Exception{
        try {
            String emailContent = preparerEmailValidation(validation_token);
            UtilitaireEnvoieEmail.envoyerEmail(email, emailContent);
        } catch (Exception e) {
            throw e;
        }
    }

    // verifier le validation_token
    private Utilisateur verifierToken(Connection conn, String validationToken) throws Exception {
        String query = "SELECT * FROM utilisateur_temp " +
                    "WHERE validation_token = ? ";
        String updateQuery = "UPDATE utilisateur_temp SET is_used = 'Y' WHERE id = ?";
        Utilisateur util = null;

        try (PreparedStatement selectStmt = conn.prepareStatement(query);
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            
            String hashToken = UtilitaireAuthentification.hashPassword(validationToken);
            selectStmt.setString(1, hashToken);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp expirationDate = rs.getTimestamp("expiration_date");

                    if (rs.getString("is_used").equalsIgnoreCase("Y")) {
                        throw new Exception("Votre compte a bien été crée, Veuillez vous reconnecter");
                    }
                    if (expirationDate != null && expirationDate.before(new Timestamp(System.currentTimeMillis()))) {
                        throw new Exception("Le token a expiré");
                    }

                    String email = rs.getString("email");
                    String mdp = rs.getString("mdp");

                    updateStmt.setInt(1, rs.getInt("id"));
                    updateStmt.executeUpdate();
                    util = new Utilisateur();
                    util.setEmail(email);
                    util.setMdp(mdp);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        if (util!=null) {
            return util;
        }else{
            throw new Exception("Veuiller creer un compte!");
        }
        
    }

    public void inscription(String email, String mdp) throws Exception{
        try(Connection connection = Connexion.connect()) {
            String validationToken = insererUtilisateurTemporaire(email, mdp, connection);
            envoyerEmailValidation(email, validationToken);     
            connection.commit();       
        } catch (Exception e) {
           throw e;
        }
    }
    
    public void verifierEmail(String validationToken)throws Exception{
        try(Connection connection = Connexion.connect()) {
            Utilisateur util = verifierToken(connection, validationToken);
            inserer(util.getEmail(), util.getMdp(), connection);
            connection.commit();
        } catch (Exception e) {
            throw e;
        }
    }
    
}