package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private final Properties properties;

    /**
     * Charge un fichier de propriétés à partir du classpath.
     * 
     * @param propertiesFilePath Chemin relatif du fichier de propriétés dans le classpath.
     * @throws IllegalArgumentException Si le fichier est introuvable ou illisible.
     */
    public PropertiesLoader(String propertiesFilePath) {
        properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFilePath)) {
            if (input == null) {
                throw new IllegalArgumentException("Le fichier de propriétés est introuvable : " + propertiesFilePath);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement du fichier de propriétés : " + propertiesFilePath, e);
        }
    }

    /**
     * Récupère la valeur associée à une clé dans le fichier de propriétés.
     * 
     * @param key Clé pour laquelle récupérer la valeur.
     * @return La valeur associée à la clé, ou null si la clé n'existe pas.
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
