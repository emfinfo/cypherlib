package ch.jcsinfo.cypher.helpers;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.KeyGenerator;

/**
 * Permet de créer et gérer une clé secrète unique pour AES. Cette clé est
 * stockée dans un fichier "secret.key" à la racine du chemin des classes.
 *
 * @author Jean-Claude Stritt
 */
public class SecretKey {
  private static SecretKey instance = null;

  private static final String FIXED_KEY = "XMY556PCOMMEPAUL"; // 16 car. ou 256 bits
  private static final String ENCRYPTION_TYPE = "AES";
  private static final String SECRET_KEY_FILEPATH = "secret.key";
  private javax.crypto.SecretKey secretKey;

  private SecretKey() {
    generate();
  }

  /**
   * Méthode getIntance pour récupérer une instance unique de cette classe, car
   * celle-ci est implémentée comme un "singleton".
   *
   * @return une instance de cette classe
   */
  public synchronized static SecretKey getInstance() {
    if (instance == null) {
      instance = new SecretKey();
    }
    return instance;
  }

  /**
   * Génère une clé.
   */
  private void generate() {
    try {
      KeyGenerator keyGen = KeyGenerator.getInstance(ENCRYPTION_TYPE);
      SecureRandom random = new SecureRandom(FIXED_KEY.getBytes());
      keyGen.init(random);
      secretKey = keyGen.generateKey();
      save(secretKey.getEncoded());
    } catch (NoSuchAlgorithmException ex) {
      System.out.println(ex.getMessage());
    }
  }

  /**
   * Teste si le fichier pour la clé secrète existe déjà.
   *
   * @param fName le nom du fichier à tester
   * @return true s'il n'existe pas encore
   */
  private boolean isFileNotExists( String fName ) {
    File f = new File(fName);
    return !f.exists();
  }

  /**
   * Sauve la clé secrète générée dans un fichier "secret.key".
   *
   * @param keyBytes la clé secrète sous la forme d'un tableau d'octets
   */
	private void save(byte[] keyBytes) {
    if (isFileNotExists(SECRET_KEY_FILEPATH)) {
      FileOutputStream fos;
      try {
        fos = new FileOutputStream(SECRET_KEY_FILEPATH);
        String b64 = Base64.getEncoder().encodeToString(keyBytes);
        fos.write(b64.getBytes());
        fos.close();
      } catch (FileNotFoundException ex) {
      } catch (IOException ex) {
      }
    }
	}

  /**
   * Permet de lire la clé secrète depuis le fichier créé.
   *
   * @return la clé secrète sous la forme d'un String encodé Base64
   */
  public String load() {
    String sKey = "";
    try {
      File file = new File(SECRET_KEY_FILEPATH);
      FileInputStream fis = new FileInputStream(SECRET_KEY_FILEPATH);
      byte[] secretBytes = new byte[(int) file.length()];
      fis.read(secretBytes);
      fis.close();
      sKey = new String(secretBytes);
    } catch (FileNotFoundException ex) {
    } catch (IOException ex) {
    }
    return sKey;
  }
}
