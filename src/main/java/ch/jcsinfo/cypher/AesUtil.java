package ch.jcsinfo.cypher;

import ch.jcsinfo.cypher.helpers.Convert;
import ch.jcsinfo.cypher.helpers.Generate;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Classe de méthodes "utilitaires" pour gérer les données encryptées d'un login
 * ou d'une licence utilisteur.
 *
 * @author jcstritt
 */
public class AesUtil {
  private final static String[] PNAME = {"li", "cen", "se", ".pro", "per", "ties"};
  private final static long MILLISECONDS_PER_DAY = 86400000;
  private final static int[] MAX_DAYS = {365, 30};
  private static Date curTimestamp = new Date(0);

  private static int getDaysBetweenTwoDates(Date theLaterDate, Date theEarlierDate) {
    long delta = theEarlierDate.getTime() - theLaterDate.getTime();
    return (int) (delta / MILLISECONDS_PER_DAY);
  }

  private static String dateToString(Date date) {
    String sDate;
    Locale locale = Locale.getDefault();
    SimpleDateFormat ldf = new SimpleDateFormat("d MMMM yyyy", locale);
    sDate = ldf.format(date);
    return sDate;
  }
  
  /**
   * Extraire les paramètres encryptés AES des données d'un login.
   * Ces paramètres, séparés par un séparateur spécial, sont au minimum 4 :<br>
   * - username<br>
   * - domain<br>
   * - password<br>
   * - timestamp<br>
   *
   * @param data les paramètres encryptées d'un login
   * @return un tableau de 4 paramètres au minimum
   */
  public static String[] extractParameters(String data) {
    String SEPARATOR = "♂♥♀";
    String[] t = {"", "", "", "0"};

    // on ne traite que si les données contiennent plus de 64 caractères
    if (data != null && data.length() > 64) {

      // récupération du sel et du vecteur d'initialisation
      String salt = data.substring(0, 32);
      String iv = data.substring(32, 64);

      // génère la même clé que le client doit générer
      String passPhrase = Generate.passPhrase();

      // crée l'utilitaire pour le décryptage
      AES aesUtil = new AES(128, 1000);

      // convertit les données hexadécimales en Base64 (nécessaire pour le décryptage)
      byte[] bytes = Convert.hexToBytes(data.substring(64));
      String b64 = Convert.toBase64(bytes);

      // décrypte les données
      String decrypted = aesUtil.decrypt(salt, iv, passPhrase, b64);

      // séparateur pour les demandes de l'API
      String sep = decrypted.contains(SEPARATOR) ? SEPARATOR : "/";

      // extraction des données
      t = decrypted.split(sep);
    }
    return t;
  }

  /**
   * Extrait le 1er paramètre des données d'un login encrypté.
   * Ce paramètre est normalement le nom d'utilisateur.
   *
   * @param data les paramètres encryptées d'un login
   * @return le nom d'utilisateur
   */
  public static String extractName(String data) {
    String result = "?";
    String[] t = extractParameters(data);
    if (t.length >= 4) {
      Date timestamp = new Date(Long.parseLong(t[3]));
      long diff = timestamp.getTime() - curTimestamp.getTime();
//      System.out.println("ts: "+timestamp.getTime()+ ", refTimestamp: "+refTimestamp.getTime()+", diff: "+diff);
      if (diff > 0) {
        result = t[0];
      }
      curTimestamp = new Date(Long.parseLong(t[3]));
    }
    return result;
  }

  /**
   * Retrouve le nom décrypté du propriétaire d'une licence stockée
   * dans un fichier de propriétés.
   *
   * @param key la clé de décryptage
   * @return le nom décrypté du propriétaire de la licence
   * @throws IOException une exceptiom s'il y a un propblème avec le fichier
   */
  public static String getLicense(String key) throws Exception {
    String result = "";

    // prépare le nom du fichier
    String fname = "";
    for (String e : PNAME) {
      fname += e;
    }

    // crée un objet reader de propriétés
    Properties pr = new Properties();

    // try-with-resources de Java 8 (pas besoin du close de l'inputStream)
    try (InputStream is = new FileInputStream(fname)) {
      // charge les propriétés
      pr.load(is);

      // décrypte la licence
      String value = pr.getProperty(Locale.getDefault().getLanguage());
      String salt = pr.getProperty("salt");
      String iv = pr.getProperty("four");
      try {
        AES aesUtil = new AES(128, 1000);
        result = aesUtil.decrypt(salt, iv, key, value);
      } catch (Exception ex) {
      }
    }
    return result;
  }
  
  /**
   * Récupère les propriétés d'une licence encryptée AES.
   * 
   * @param key la clé de décryptage de la licence
   * @param rb  un objet "ResourceBundle" pour retrouver le nom par défaut dans les resources
   * @param startTime la date en [ms] où le logiciel a été installée
   * 
   * @return un object Properties avec les 5 propriétés de la licence (valid, owner, maxDays, maxDate, dqys)
   */
  public static Properties getLicenseProperties(String key, ResourceBundle rb, long startTime) {
    Properties pr = new Properties();

    // trouve la licence d'après la clé
    String license;
    try {
      license = getLicense(key);
    } catch (Exception ex) {
      license = "";
    }

    // détermine la validité de la licence
    boolean valid = license.endsWith("*");
    Date nowDate = new GregorianCalendar().getTime();
    Date startDate = new Date(startTime);
    int maxDays = (license.endsWith("$") ? MAX_DAYS[0] : MAX_DAYS[1]);
//    maxDays += (Year.isLeap(getYear(startDate))) ? 1 : 0;
    int days = getDaysBetweenTwoDates(startDate, nowDate);
    pr.put("valid", valid || days <= maxDays);

    // détermine le nom du propriétaire de la licence
    String owner;
    if (license.endsWith("$") || license.endsWith("*")) {
      owner = license.substring(0, license.length() - 1);
    } else {
      owner = rb.getString("app.licence.name").trim();
    }
    pr.put("owner", owner);
    
    // détermine le nombre de jours valides pour la license
    if (valid) {
      pr.put("maxdays", "OO");      
    } else {
//      pr.put("maxdays", "" + days + " / " + maxDays);
      pr.put("maxdays", "" + maxDays);
    }
    
    // détermine la date de fin de la licence
    String maxdate = "OO";
    Date endDate = new Date(startTime + maxDays * MILLISECONDS_PER_DAY);
    if (!valid) {
      maxdate = dateToString(endDate);
    }   
    pr.put("maxdate", maxdate);
    
    // jours écoulés
    if (valid) {
      pr.put("days", "" + days + " / OO");
    } else {
      pr.put("days", "" + days + " / " + maxDays);
    }
    
    // retourne les propriétés de la licence
    return pr;
  }

}
