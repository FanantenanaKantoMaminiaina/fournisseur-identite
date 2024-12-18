package util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class Utilitaire {
   /*
    *
    * Fonction pour int
    *
    * Fonction pour double
    *
    * Fonction pour String
    *
    * Fonction pour Date sql
    * 
    * Fonction pour Object
    *
    * Fonction pour Timestamp
    *
    * Fonction pour Email
    *
    */

    static Random random = new Random();

// Fonction pour int
    /**
     * Genere un entier aleatoire entre les bornes specifiees inclusivement.
     * 
     * @param min La borne inferieure (incluse).
     * @param max La borne superieure (incluse).
     * @return Un entier aleatoire entre min et max.
     */
    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("La valeur min doit être inferieure ou egale a max.");
        }
        return random.nextInt((max - min) + 1) + min;
    }


// Fonction pour double
    /**
     * Fonction pour convertir une longueur string avec unite ou pas en nombre en metre
     * 
     * @param vals
     * @return
     * @throws Exception
     */
    public static double convert(String vals) throws Exception{
        double valeur = 0;
        String[] rep=vals.split(" ");

        valeur = Double.parseDouble(rep[0]);
        if (rep.length>1) {
            if (rep[1].equalsIgnoreCase("cm")) {
                valeur = valeur/100; 
            }else if (rep[1].equalsIgnoreCase("m")) {
                valeur = valeur;
            }           
        }

        return valeur;
    }

    /**
     * Genere un nombre aleatoire entre les bornes specifiees inclusivement.
     * 
     * @param min La borne inferieure (incluse).
     * @param max La borne superieure (incluse).
     * @return Un nombre double aleatoire entre min et max.
     */
    public static double randomDouble(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("La valeur min doit être inferieure ou egale a max.");
        }
        return min + (max - min) * random.nextDouble();
    }

    /**
     * Genere un nombre prix + prix * nombre aleatoire entre les 2 pourcentages donnees.
     * 
     * @param min La borne inferieure (incluse).
     * @param max La borne superieure (incluse).
     * @return Un nombre double aleatoire entre min et max.
     */
    public static double randomPrix(double prix,double minPourcentage, double maxPourcentage) {
        double pourcentage = randomDouble(minPourcentage, maxPourcentage);

        return prix + (prix * pourcentage / 100);
    }

    /**
     * Verifier si c'est un nombre
     * @param value
     * @return
     */
    public static boolean isNumeric(String value) {
        try {
            double d = Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }


// Fonction pour String
    /**
     * Verifier si un String est vide
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

// Fonction pour Date
    /**
     * Fonction ayant pour resultat le jour de la semaine correspondant a la
     * date donnee en entier (1 pour dimanche, 2 pour lundi, ...)
     *
     * @param daty
     * @return
     */
    public static int dayOfDate(Date daty) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(daty);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        return day;
    }

    /**
     * 
     * Verifie si la date est un jour ouvrable
     *
     * @param daty
     * @return
     * @throws Exception
     */
    public static boolean estJourOuvrable(Date daty) throws Exception {
        boolean result = false;
        if (Utilitaire.dayOfDate(daty) != 1 && Utilitaire.dayOfDate(daty) != 7) {
            result = true;
        }
        return result;
    }

    /**
     * 
     * Pour avoir une random date entre 2 dates limites
     * 
     * @param min
     * @param max
     * @return
     */
    public static Date random(Date min, Date max) {
        if (min.after(max)) {
            throw new IllegalArgumentException("La date min doit être avant ou egale a la date max.");
        }

        long minTime = min.getTime();
        long maxTime = max.getTime();
        long randomTime = ThreadLocalRandom.current().nextLong(minTime, maxTime + 1);
        return new Date(randomTime);
    }

    /**
     * 
     * Fonction pour faire une random entre 2 dates limitea qui retourne une date ouvrable
     * 
     * @param min
     * @param max
     * @return
     * @throws Exception
     */
    public static Date randomDateOuvrable(Date min, Date max) throws Exception{
        Date date = random(min, max);
        while (!Utilitaire.estJourOuvrable(date)) {
            date = random(min, max);
        }
        return date;
    }


    /**
     * Modifier le format du date
     * 
     * @param inputDate
     * @return
     * @throws Exception
     */
    public static String convertDate(String inputDate) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return outputFormat.format(inputFormat.parse(inputDate));
    }

    /**
     * Verifier une format pour l'heure,date et dateheure
     * Empêche les dates non valides (par exemple, 2024-02-30)
     * 
     * @param format
     * @param value
     * @return
     */
    private static boolean isValidFormat(String format, String value) {
        if (value == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Verifier si c'est un date avec une format donnee
     * Exemple : ("yyyy-MM-dd")
     * 
     * @param value
     * @param format
     * @return
     */
    public static boolean isDate(String value,String format) {
        return isValidFormat(format, value);
    }

// Fonction pour Object  
    /**
     * 
     * Fonction pour faire une random pour un tableau d'objet et qui retourne une objet
     * 
     * @param objects
     * @return
     */
    public static Object randomObect(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return null;
        }
        int index = random.nextInt(objects.length);
        return objects[index];
    }

// Fonction pour Timestamp
    /**
     * 
     * Modifier le string dans le formulaire en string compatible pour le format Timestamp
     * 
     * @param inputDateTime
     * @return
     * @throws Exception
     */
    public static String convertToTimestamp(String inputDateTime) throws Exception {
        if (inputDateTime != null && !inputDateTime.isEmpty()) {
            try {
                inputDateTime = inputDateTime.replace("T", " ");
                if (inputDateTime.length() == 16) {
                    inputDateTime += ":00";
                }
                return inputDateTime;
            } catch (IllegalArgumentException e) {
                throw new Exception("Error: Invalid date format. Please use 'yyyy-MM-dd HH:mm:ss'.", e);
            }
        } else {
            throw new Exception("Error: Date time string is null or empty.");
        }
    }

    /**
     * Verfier si c'est une dateheure
     * 
     * @param value
     * @return
     */
    public static boolean isDateTime(String value) {
        return isValidFormat("yyyy-MM-dd HH:mm:ss", value);
    }

     /**
     * Fonction pour obtenir la date et heure actuelle
     * 
     * @return Timestamp
     */
    public static Timestamp getNow() {
        LocalDateTime now = LocalDateTime.now();
        return Timestamp.valueOf(now);
    }

     /**
     * Fonction pour ajouter un intervalle en secondes a un Timestamp
     * 
     * @param timestamp
     * @param seconde
     * @return Timestamp
     */
    public static Timestamp addSeconds(Timestamp timestamp, int seconds) {
        long millisecondsToAdd = seconds * 1000L;
        return new Timestamp(timestamp.getTime() + millisecondsToAdd);
    }

// Fonction pour Email
    /**
     * Verifier si c'est une email
     * @param value
     * @return
     */
    public static boolean isEmail(String value) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(value).matches();
    }
}
