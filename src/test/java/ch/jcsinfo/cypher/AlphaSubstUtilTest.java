package ch.jcsinfo.cypher;

import ch.jcsinfo.helpers.StackTracer;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Classe de tests pour les méthodes d'encryptage/décryptage par substitution d'alphabets.
 *
 * @author jcstritt
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlphaSubstUtilTest {
  private static String TXT = "06h30, Bonjour les amis de la pêche en Gruyère !";

  @Test
  public void test01_encrypt() {
    StackTracer.printCurrentTestMethod();
    String encrypted = AlphaSubstUtil.encrypt(TXT);
    StackTracer.printTestInfo(TXT, encrypted);
    assertTrue(!encrypted.equals(TXT));
  }

  @Test
  public void test02_decrypt() {
    StackTracer.printCurrentTestMethod();
    String encrypted = AlphaSubstUtil.encrypt(TXT);
    String decrypted = AlphaSubstUtil.decrypt(encrypted);
    StackTracer.printTestInfo(encrypted, decrypted);
    assertTrue(decrypted.equals(TXT));
  }

}