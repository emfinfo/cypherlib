package ch.emf.cypher;

import ch.emf.helpers.Convert;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Classe pour encrypter et décrypter en AES en 128 ou 256 bits. 
 * Utilise la méthode AES/CBC/PKCS5Padding qui nécessite un sel pour
 * ne pas avoir 2x la même clé pour un même mot de passe, mais
 * également un vecteur d'initialisation (nommé "iv" ou "four") 
 * pour démarrer l'encryptage toujours différement.
 * <br>
 * Selon sources et explications ici :<br>
 * See https://www.linkedin.com/pulse/jshtml5-java-encryption-using-aes-128bit256bit-subhadip-pal/
 *
 * @author Subhadip Pal / J.-C. Stritt
 */
public class AesUtil {

  private final int keySize;
  private final int iterationCount;
  private final Cipher cipher;

  private IllegalStateException fail(Exception e) {
    return new IllegalStateException(e);
  }

  /**
   * Constructeur.
   *
   * @param keySize        la taille de la clé (128 ou 256)
   * @param iterationCount le nb d'itérations permettant de changer les spécificités de la clé
   */
  public AesUtil(int keySize, int iterationCount) {
    this.keySize = keySize;
    this.iterationCount = iterationCount;
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw fail(e);
    }
  }

  /**
   * Effectue l'encryptage ou le décryptage final.
   * 
   * @param encryptMode Cipher.ENCRYPT_MODE ou Cipher.DECRYPT_MODE
   * @param key la clé finale généré
   * @param iv le vecteur d'inialisation à utiliser
   * @param bytes le tableau d'octets à crypter ou décrypter
   * 
   * @return le tableau d'octets cryptés ou décryptés
   */
  private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
    try {
      cipher.init(encryptMode, key, new IvParameterSpec(Convert.toHex(iv)));
      return cipher.doFinal(bytes);
    } catch (InvalidKeyException
      | InvalidAlgorithmParameterException
      | IllegalBlockSizeException
      | BadPaddingException e) {
      throw fail(e);
    }
  }

  /**
   * Grâce à un sel, génère une clé toujours différente pour un même mot de passe.
   * 
   * @param salt un sel (généralement créé aléatoirement, voir la classe Generate)
   * @param passphrase une phrase faisant office de mot de passe
   * 
   * @return la clé AES générée
   */
  private SecretKey generateKey(String salt, String passphrase) {
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), Convert.toHex(salt), iterationCount, keySize);
      SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
      return key;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw fail(e);
    }
  }

  /**
   * Encrypte un texte et l'encode en Base64.
   *
   * @param salt       un sel (généralement aléatoire, voir la classe Generate)
   * @param iv         un vecteur d'initialisation (généralement aléatoire, voir la classe Generate)
   * @param passphrase une phrase faisant office de mot de passe
   * @param plaintext  le texte à encrypter
   * 
   * @return le texte encrypté et encodé en Base64
   */
  public String encrypt(String salt, String iv, String passphrase, String plaintext) {
    try {
      SecretKey key = generateKey(salt, passphrase);
      byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.getBytes("UTF-8"));
      return Convert.toBase64(encrypted);
    } catch (UnsupportedEncodingException e) {
      throw fail(e);
    }
  }

  /**
   * Déchiffre un texte encrypté et codé Base64.
   *
   * @param salt       un sel (généralement transmis au serveur dans une application web)
   * @param iv         un vecteur d'initialisation (généralement transmis aussi)
   * @param passphrase une phrase faisant office de mot de passe
   * @param ciphertext le texte encrypté et encodé Base64 qu'il faut décrypter
   * 
   * @return le texte décrypté, donc en clair (UTF-8)
   */
  public String decrypt(String salt, String iv, String passphrase, String ciphertext) {
    try {
      SecretKey key = generateKey(salt, passphrase);
      byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, Convert.toBase64(ciphertext));
      return new String(decrypted, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw fail(e);
    }
  }

}
