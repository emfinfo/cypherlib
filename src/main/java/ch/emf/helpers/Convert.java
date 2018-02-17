package ch.emf.helpers;

import ch.emf.cypher.AlphaSubstUtil;
import ch.emf.cypher.XorUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Classe permettant des conversions spéciales (String, Base64 et hexadécimal).
 *
 * @author jcstritt
 */
public class Convert {
 
  /**
   * Cette méthode a été créée pour de vieux programmes fonctionnant encore
   * en DOS. Elle effectue un décryptage par "manipulation de bits" (XOR)
   * puis par substitution d'alphabets entre un texte stocké sous la forme
   * d'entiers courts (SHORT) codés en DOS CP850 et une clé formée
   * d'une chaîne de caractères.
   *
   * @param buffer un texte stocké dans un tableau de SHORT
   * @param key une chaîne de caractères représentatnt la clé
   * @return le texte décrypté (en clair)
   */
  public static String toString(short[] buffer, String key) {
    String result = XorUtil.decrypt(buffer, key);
    return AlphaSubstUtil.decrypt(result);
  }

  /**
   * Convertit un tableau d'octets en une chaîne de caractères Base64.<br>
   * <br>
   * Base64 utilisant 4 caractères imprimables pour 3 octets binaires, 
   * il faut noter que la taille finale de la chaîne retournée sera 4/3
   * x la taille du tableau d'octets (encore arrondie pour pouvoir être 
   * divisible par 3).<br>
   * <br>
   * Exemple: si 16 octets dans le tableau, c'est 18 / 3 * 4 = 24 car.
   * 
   * @param bytes un tableau d'octets
   * @return la chaîne de caractères correspondante Base64 
   */
  public static String toBase64(byte[] bytes) {
    return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
  }

  /**
   * Convertit une chaîne encodée en Base64 dans un tableau d'octets.
   * 
   * @param txt une chaîne de caractères encodée en Base64
   * @return le tableau d'octets correspondant
   */
  public static byte[] toBase64(String txt) {
    return org.apache.commons.codec.binary.Base64.decodeBase64(txt);
  }

  /**
   * Convertit un tableau d'octets en une chaîne de caractères hexadécimale.<br>
   * <br>
   * A noter que la taille finale de la chaîne retournée sera 2x la taille
   * du tableau d'octets fourni.<br>
   * <br>
   * Exemple: si le tableau contient 16 octets, la taille finale sera de 32 caractères, 
   * car chaque octet sera codé de 00 à ff.
   * 
   * @param bytes un tableau d'octets
   * @return la chaîne de caractères correspondante en hexadécimal 
   */
  public static String toHex(byte[] bytes) {
    return Hex.encodeHexString(bytes);
  }

  /**
   * Convertit une chaîne encodée en hexadécimal dans un tableau d'octets.
   * 
   * @param txt une chaîne de caractères en hexadécimal
   * @return le tableau d'octets correspondant
   */
  public static byte[] toHex(String txt) {
    try {
      return Hex.decodeHex(txt.toCharArray());
    } catch (DecoderException e) {
      throw new IllegalStateException(e);
    }
  }

}
