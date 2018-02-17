package ch.emf.helpers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
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
   * Permet de hâcher une chaîne de caractères avec un algorithme spécifié.
   *
   * @param txt une chaîne de caractère à hâcher
   * @param algorithm l'algorithme à utiliser (ex: "SHA-256")
   * 
   * @return la chaîne hâchée encodée en hexadécimal
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
    String KA = "qWe";
    String KB = "PoI";
    Date date = new Date();
    Locale locale = Locale.getDefault();
    SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MM-dd", locale);
    String sDate = ldf.format(date);
    return KA + sDate + KB;
  }

  /**
   * Génére une suite d'octets aléatoires encodés au final en hexadécimal.
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
   * Génére une suite d'octets aléatoires encodés au final en Base64.
   * Base64 utilisant 4 caractères imprimables pour 3 octets binaires, 
   * il faut noter que la taille finale de la chaîne retournée sera 4/3 *
   * la taille demandé (arrondie pour pouvoir être divisible par 3). 
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
