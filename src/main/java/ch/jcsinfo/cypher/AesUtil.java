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
   * Retourne le nom du propriétaire d'une licence. S'il n'est pas trouvé
   * dans le fichier "licence.properties", un nom par défaut est récupéré
   * depuis une resource de l'application (avec le nom "app.licence.name").
   *
   * @param key la clé de décryptage pour le fichier de licence
   * @param rb  un objet "ResourceBundle" pour retrouver un nom
   * @return le nom du propriétaire de la licence ou un nom par défaut
   */
  public static String getLicenseOwner(String key, ResourceBundle rb) {
    String result;
    String licence;
    try {
      licence = getLicense(key);
    } catch (Exception ex) {
      licence = "";
    }
    if (licence.endsWith("$") || licence.endsWith("*")) {
      result = licence.substring(0, licence.length() - 1);
    } else {
      result = rb.getString("app.licence.name").trim();
    }
//    System.out.println("getLicenseOwner: key=" + key + ", licence=" + licence + ", result=" + result);
    return result;
  }

  /**
   * Teste si une licence est valide.
   *
   * @param key       la clé de décryptage
   * @param startTime la date en [ms] où le logiciel a été installée
   * @return true si la licence est valide, false autrement
   */
  public static boolean isLicenseOk(String key, long startTime) {
    String licence;
    try {
      licence = getLicense(key);
    } catch (Exception ex) {
      licence = "";
    }
    boolean licenceOk = licence.endsWith("*");
    int maxDays = (licence.endsWith("$") ? 30 : 15);
    Date nowDate = new GregorianCalendar().getTime();
    Date startDate = new Date(startTime);
    int days = getDaysBetweenTwoDates(startDate, nowDate);
    licenceOk = licenceOk || days <= maxDays;

//    System.out.println(
//      "Licence ok: " + licenceOk
//      + ", start date: " + dateToString(startDate) + " (" + startDate.getTime() + ")"
//      + ", now date: " + dateToString(nowDate) + " (" + nowDate.getTime() + ")"
//      + ", days=" + days);

    return licenceOk;
  }
  
  /**
   * Retourne la date de fin de la licence ou le signe ∞ (infini) si
   * la licence est définitive.
   * 
   * @param key       la clé de décryptage
   * @param startTime la date en [ms] où le logiciel a été installée
   * @return un string contenant la date de fin (en utilisant la Locale par défaut)
   */
  public static String getLicenseEnd(String key, long startTime) {
    String result = "OO";
    String licence;
    try {
      licence = getLicense(key);
    } catch (Exception ex) {
      licence = "";
    }
    boolean licenceOk = licence.endsWith("*");
    int maxDays = (licence.endsWith("$") ? 30 : 15);
    Date endDate = new Date(startTime + maxDays * MILLISECONDS_PER_DAY);
    if (!licenceOk) {
      result = dateToString(endDate);
    }
    return result;
  }

}
