package ch.jcsinfo.cypher.helpers;

import ch.jcsinfo.cypher.AlphaSubstUtil;
import ch.jcsinfo.cypher.XorUtil;
import java.util.Base64;

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
   * @param key    une chaîne de caractères représentatnt la clé
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
    return Base64.getEncoder().encodeToString(bytes);
  }

  /**
   * Convertit une chaîne encodée en Base64 dans un tableau d'octets.
   *
   * @param txt une chaîne de caractères encodée en Base64
   * @return le tableau d'octets correspondant
   */
  public static byte[] toBase64(String txt) {
    return Base64.getDecoder().decode(txt);
  }

  /**
   * Convertit un caractère hexadécimal en digit.
   * 
   * @param hexChar un caractère hexadécimal
   * @return un entier représentatnt le caractère hexadécimal
   */
  public static int toDigit(char hexChar) {
    int digit = Character.digit(hexChar, 16);
    if (digit == -1) {
      throw new IllegalArgumentException(
        "Invalid Hexadecimal Character: " + hexChar);
    }
    return digit;
  }

  /**
   * Convertit un nombre 0..15 dans un octet vers une représentation
   * hexdécimale sur 2 positions.
   * 
   * @param num un nombre (0 à 255) dans un octet
   * @return la représentation hexadécimal de ce nombre
   */
  public static String byteToHex(byte num) {
    char[] hexDigits = new char[2];
    hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
    hexDigits[1] = Character.forDigit((num & 0xF), 16);
    return new String(hexDigits);
  }

  /**
   * Convertit un string représentant un nombre hexadécimal sur 2 digits 
   * vers une représentation numérique dans un octet.
   * 
   * @param hexString le string avec de l'hexa sur 2 digits
   * @return l'octet contenant la valeur de la représentation hexadécimale
   */
  public static byte hexToByte(String hexString) {
    int firstDigit = toDigit(hexString.charAt(0));
    int secondDigit = toDigit(hexString.charAt(1));
    return (byte) ((firstDigit << 4) + secondDigit);
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
    StringBuilder hexStringBuffer = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      hexStringBuffer.append(byteToHex(bytes[i]));
    }
    return hexStringBuffer.toString();
  }

  /**
   * Décode une chaîne en hexadécimal vers un tableau d'octets.
   *
   * @param hexString une chaîne de caractères en hexadécimal
   * @return le tableau d'octets correspondant
   */
  public static byte[] hexToBytes(String hexString) {
    if (hexString.length() % 2 == 1) {
      throw new IllegalArgumentException("Invalid hexadecimal String supplied.");
    }

    byte[] bytes = new byte[hexString.length() / 2];
    for (int i = 0; i < hexString.length(); i += 2) {
      bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
    }
    return bytes;
  }

}
