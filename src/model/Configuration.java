package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Configuration {

    private int idConfiguration;
    private int dureeVieToken;
    private int dureeViePin;
    private int limiteTentative;

    public Configuration() { }

    public Configuration( int idConfiguration, int dureeVieToken, int dureeViePin, int limiteTentative) {
        this.idConfiguration = idConfiguration;
        this.dureeViePin = dureeViePin;
        this.dureeVieToken = dureeVieToken;
        this.limiteTentative = limiteTentative;
    }

    public int getIdConfiguration() {
        return idConfiguration;
    }
    public void setIdConfiguration(int idConfiguration) {
        this.idConfiguration = idConfiguration;
    }
    public int getDureeVieToken() {
        return dureeVieToken;
    }
    public void setDureeVieToken(int dureeVieToken) {
        this.dureeVieToken = dureeVieToken;
    }
    public int getDureeViePin() {
        return dureeViePin;
    }
    public void setDureeViePin(int dureeViePin) {
        this.dureeViePin = dureeViePin;
    }
    public int getLimiteTentative() {
        return limiteTentative;
    }
    public void setLimiteTentative(int limiteTentative) {
        this.limiteTentative = limiteTentative;
    }
    
    public int save(Connection connection) throws SQLException {
        String query = "INSERT INTO configuration (duree_vie_token, duree_vie_pin, limite_tentative) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, this.dureeVieToken);
            statement.setInt(2, this.dureeViePin);
            statement.setInt(3, this.limiteTentative);
            return statement.executeUpdate();
        }
    }

  
    public static Configuration getConfiguration(Connection connection) throws SQLException {
        String query = "SELECT id_configuration, duree_vie_token, duree_vie_pin, limite_tentative FROM configuration";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Configuration(
                            resultSet.getInt("id_configuration"),
                            resultSet.getInt("duree_vie_token"),
                            resultSet.getInt("duree_vie_pin"),
                            resultSet.getInt("limite_tentative")
                    );
                }
            }
        }
        return null; 
    }

    public int update(Connection connection) throws SQLException {
        String query = "UPDATE configuration SET duree_vie_token = ?, duree_vie_pin = ? , limite_tentative = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, this.dureeVieToken);
            statement.setInt(2, this.dureeViePin);
            statement.setInt(3, this.limiteTentative);
            return statement.executeUpdate();
        }
    }
}
