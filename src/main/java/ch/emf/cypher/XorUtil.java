package ch.emf.cypher;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Encryptage - décryptage par la méthode de manipulation des bits (XOR) entre
 * un texte et une clé donnée. Le résultat est converti en Base64 pour
 * une meilleure lisibilité.<br>
 * <br>
 * Ne pas utiliser en production, car trop simple (uniquement à but pédagogique
 * ou pour de très vieux logiciels).
 *
 * @author jcstritt
 */
public class XorUtil {
  public static final Charset DOS_CHARSET = Charset.forName("CP850");

  /**
   * Méthode privée qui effectue la "manipulation de bits" par
   * une opération XOR.
   *
   * @param expBytes l'expression à encrypter/décrypter
   * @param keyBytes la clé qui permet l'encryptage/décryptage
   * @return un tableau d'octets avec l'expression encryptée/décryptée
   */
  public static byte[] xorProcess(byte[] expBytes, byte[] keyBytes) {
    byte[] result = new byte[expBytes.length];
    for (int i = 0; i < expBytes.length; i++) {
      result[i] += expBytes[i] ^ keyBytes[i % keyBytes.length];
    }
    return result;
  }

  /**
   * Encryptage par "manipulation de bits" (XOR).
   *
   * @param txt le texte à encrypter
   * @param key la clé qui permet l'encryptage
   * @param cs un objet Charset pour définir l'encodage de la source
   * @return le texte encrypté et encodé en Base64
   */
  public static String encrypt(String txt, String key, Charset cs) {
    byte[] txtBytes = txt.trim().getBytes(cs);
    byte[] keyBytes = key.trim().getBytes(cs);
    return Base64.getEncoder().encodeToString(xorProcess(txtBytes, keyBytes));
  }

  /**
   * Décryptage par "manipulation de bits" (XOR).
   *
   * @param txt le texte à décrypter (encodé Base64)
   * @param key la clé qui permet le décryptage
   * @param cs un objet Charset pour définir l'encodage du résultat
   * @return le texte décrypté (en clair)
   */
  public static String decrypt(String txt, String key, Charset cs) {
    byte[] txtBytes = Base64.getDecoder().decode(txt.trim());
    byte[] keyBytes = key.trim().getBytes(cs);
    byte[] resultBytes = xorProcess(txtBytes, keyBytes);
    return new String(resultBytes, cs);
  }

  /**
   * Cette méthode a été créée pour de vieux programmes fonctionnant encore
   * en DOS. Elle effectue un décryptage par "manipulation de bits" (XOR)
   * entre un texte stocké sous la forme d'entiers courts (SHORT) codés
   * en DOS CP850 et une clé formée d'une chaîne de caractères.
   *
   * @param buffer le texte stocké dans un tableau de SHORT (buffer)
   * @param key une chaîne de caractères représentatnt la clé
   * @return le texte décrypté (en clair)
   */
  public static String decrypt(short[] buffer, String key) {
    String result = "";
    if (buffer.length > 0) {
      byte[] bytesBuff = new byte[buffer.length];
      byte[] keyBytes = key.trim().getBytes();
      for (int i = 0; i < buffer.length; i++) {
        bytesBuff[i] = (byte) (buffer[i]);
      }
      byte[] resultBytes = xorProcess(bytesBuff, keyBytes);
      result = new String(resultBytes, DOS_CHARSET);
    }
    return result;
  }

}
