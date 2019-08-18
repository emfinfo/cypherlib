package ch.emf.cypher.helpers;

import ch.emf.cypher.AesUtil;
import java.util.Date;

/**
 * Classe de méthodes "utilitaires" pour gérer les données encryptées d'un login.
 *
 * @author jcstritt
 */
public class Utils {

  private static Date curTimestamp = new Date(0);

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
      AesUtil aesUtil = new AesUtil(128, 1000);

      // convertit les données hexadécimales en Base64 (nécessaire pour le décryptage)
      byte[] bytes = Convert.toHex(data.substring(64));
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
   * Extrait le 1er paramètre des données d'un login encrsypté.
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
}
