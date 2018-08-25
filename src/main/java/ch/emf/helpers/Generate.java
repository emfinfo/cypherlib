package ch.emf.helpers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * Classe pour générer des empreintes ou des textes en hexadécimal ou Base64.
 *
 * @author jcstritt
 */
public class Generate {

  /**
   * Génère une empreinte (hash, digest) pour une chaîne de caractères et un algorithme fournis.
   *
   * @param txt une chaîne de caractère à hâcher
   * @param algorithm l'algorithme à utiliser (ex: "SHA-256")
   *
   * @return la chaîne hâchée et encodée en hexadécimal
   */
  public static String hash(String txt, String algorithm) {
    String hash = "";
    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      md.update(txt.getBytes("UTF-8"));
      hash = Convert.toHex(md.digest());
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
//      System.out.println("Erreur: " + ex.getMessage());
    }
    return hash;
  }

  /**
   * Génère une phrase (texte) pouvant faire office de mot de passe.
   *
   * @return la phrase pouvant faire office de mot de passe
   */
  public static String passPhrase() {
//    Date date = new Date();

    // récupère la date GMT
    Calendar time = Calendar.getInstance();
    time.add(Calendar.MILLISECOND, -time.getTimeZone().getOffset(time.getTimeInMillis()));
    Date date = time.getTime();

    Locale locale = Locale.getDefault();
    SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MM-dd", locale);
    String sDate = ldf.format(date);
    return Convert.toBase64(("qWe" + sDate + "PoI").getBytes());
  }

  /**
   * Génére une suite d'octets aléatoires encodés au final en hexadécimal.<br>
   * <br>
   * A noter que la taille finale de la chaîne retournée sera 2x la taille
   * du nombre d'octets demandé. Exemple: si 16 octets sont demandés,
   * la taille finale sera de 32 caractères, car chaque octet sera codé de
   * 00 à ff.
   *
   * @param size le nombre d'octets à générer
   * @return le tableau d'octets encodé au final en hexadécimal
   */
  public static String randomHex(int size) {
    final Random r = new SecureRandom();
    byte[] salt = new byte[size];
    r.nextBytes(salt);
    return Hex.encodeHexString(salt);
  }

  /**
   * Génére une suite d'octets aléatoires encodés au final en Base64.<br>
   * <br>
   * Base64 utilisant 4 caractères imprimables pour 3 octets binaires,
   * il faut noter que la taille finale de la chaîne retournée sera 4/3
   * x la taille demandée (encore arrondie pour pouvoir être divisible par 3).<br>
   * <br>
   * Exemple: si 16 octets sont demandés, c'est 18 / 3 * 4 = 24 car.
   *
   * @param size le nombre d'octets à générer
   * @return le tableau d'octets encodé au final en hexadécimal
   */
  public static String randomBase64(int size) {
    final Random r = new SecureRandom();
    byte[] salt = new byte[size];
    r.nextBytes(salt);
    return Base64.encodeBase64String(salt);
  }

}
