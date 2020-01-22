package ch.jcsinfo.cypher;

import ch.jcsinfo.cypher.helpers.Generate;
import ch.jcsinfo.cypher.helpers.SecretKey;
import ch.jcsinfo.helpers.StackTracer;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Classe de tests pour les méthodes d'encryptage/décryptage AES.
 * 
 * @author jcstritt
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AesTest {
  private static String TXT = "06h30, Bonjour les amis de la pêche en Gruyère !";
//  private static String TXT = "user1/DEMO/Test2014/1518655806465";

  private static int keySize = 128;
  private static int iterationCount = 1000;
  private static String salt;
  private static String iv;

  private static AES aes;

  @BeforeClass
  public static void setUpClass() throws Exception {
    salt = Generate.randomHex(16);
    iv = Generate.randomHex(16);
    aes = new AES(keySize, iterationCount);
  }

  @Test
  public void test01_encrypt() throws Exception {
    StackTracer.printCurrentTestMethod();
    String passPhrase = Generate.randomBase64(16);
    String encrypted = aes.encrypt(salt, iv, passPhrase, TXT);
    StackTracer.printTestInfo(passPhrase, TXT + " len=" + TXT.length(), encrypted + " len=" + encrypted.length());
    assertTrue(!encrypted.equals(TXT));
  }

  @Test
  public void test02_decrypt() throws Exception {
    StackTracer.printCurrentTestMethod();
    String passPhrase = Generate.passPhrase();
    String encrypted = aes.encrypt(salt, iv, passPhrase, TXT);
    String decrypted = aes.decrypt(salt, iv, passPhrase, encrypted);
    StackTracer.printTestInfo(passPhrase, encrypted, decrypted);
    assertTrue(decrypted.equals(TXT));
  }

 @Test
  public void test03_encrypt() throws Exception {
    StackTracer.printCurrentTestMethod(" - secret key in file secret.key");
    String passPhrase = SecretKey.getInstance().load();
    String encrypted = aes.encrypt(salt, iv, passPhrase, TXT);
    StackTracer.printTestInfo(passPhrase, TXT, encrypted);
    assertTrue(!encrypted.equals(TXT));
  }

  @Test
  public void test04_decrypt() throws Exception {
    StackTracer.printCurrentTestMethod(" - secret key in file secret.key");
    String passPhrase = SecretKey.getInstance().load();
    String encrypted = aes.encrypt(salt, iv, passPhrase, TXT);
    String decrypted = aes.decrypt(salt, iv, passPhrase, encrypted);
    StackTracer.printTestInfo(passPhrase, encrypted, decrypted);
    assertTrue(decrypted.equals(TXT));
  }  
  
  @Test
  public void test05_prod() throws Exception {
    String customer[] = {"Dupond SA$", "Dupond AG$", "Dupond LTD$"};
    StackTracer.printCurrentTestMethod(" - special license key");
//    String passPhrase = SecretKey.getInstance().load();
    String passPhrase = Generate.passPhrase();    
    
    StackTracer.printSimpleInfo("salt", salt);      
    StackTracer.printSimpleInfo("four", iv);      
    StackTracer.printSimpleInfo("pass", passPhrase); 
    aes = new AES(keySize, iterationCount);
    for (String c : customer) {
      String encrypted = aes.encrypt(salt, iv, passPhrase, c);
      String decrypted = aes.decrypt(salt, iv, passPhrase, encrypted);
      System.out.println(encrypted);
      System.out.println("    " + decrypted);
    }
    System.out.println();
  }  
  
}
