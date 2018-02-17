package ch.emf.cypher;

/**
 * Encryptage - décryptage par la méthode de substitution d'alphabets.<br>
 * <br>
 * Ne pas utiliser en production, car trop simple (uniquement à but pédagogique
 * ou pour de très vieux logiciels).
 *
 * @author jcstritt
 */
public class AlphaSubstUtil {

  private static final String[] ALPHABETS = {
    "1AQW2 ZSX3ED(C4RFV)5TGB6*YHN7UJ8IK9OL0PMazertyuiopmlkjhgfdsqwxcvbn",
    "aetuopiyrzqdgjlmkhfsw(cbnv xN1B*2V3C4X5W6)Q7S8D9F0MLKJHGAPZOEIRUTY",
    "qWA0192XsZ8374EdC65VfRTgBNh*YUjIkOlmPMpoLiKJuyHn(t GbrFveD)czSxaQw",
    "XYZACB*FDEHIJMLKNOPSQRTUVWzxycbaedfjihklmponr(qsuvtw6 52879G40)3g1"};

  private static final int SPACE_POS = ALPHABETS[0].indexOf(" ");

  /**
   * Encrypte un texte par la méthode de substitution d'alphabets.
   *
   * @param text texte à encrypter
   * @return le texte encrypté
   */
  public static String encrypt(String text) {
    String result = "";
    int j = 0;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      int p = ALPHABETS[0].indexOf(c);
      if (p >= 0) {
        result += ALPHABETS[1 + j].charAt(p);
      } else {
        result += c;
      }
      if (p == SPACE_POS) {
        j = (j + 1) % 3;
      }
    }
    return result;
  }

  /**
   * Décrypte un message encrypté par la méthode de substitution d'alphabets.
   *
   * @param msg le message à décrypter
   * @return le texte décrypté (en clair)
   */
  public static String decrypt(String msg) {
    String result = "";
    int j = 0;
    for (int i = 0; i < msg.length(); i++) {
      char c = msg.charAt(i);
      int p = ALPHABETS[1 + j].indexOf(c);
      if (p >= 0) {
        result += ALPHABETS[0].charAt(p);
      } else {
        result += c;
      }
      if (p == SPACE_POS) {
        j = (j + 1) % 3;
      }
    }
    return result;
  }

}
