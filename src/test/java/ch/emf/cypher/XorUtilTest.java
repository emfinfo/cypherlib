package ch.emf.cypher;

import ch.emf.helpers.StackTracer;
import java.nio.charset.Charset;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Classe de tests pour les méthodes d'encryptage/décryptage par manipulation de bits (XOR).
 *
 * @author jcstritt
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XorUtilTest {
  private static String TXT = "06h30, Bonjour les amis de la pêche en Gruyère !";
  private static String KEY = "Pablo" + "Picasso";
  
  private static String original[] = {"Demo AG", "Demo Ltd", "Demo SA"};
  private static String encoded[] = {"", "", ""};

  public static String fillString(int len, char ch) {
    char[] array = new char[len];
    Arrays.fill(array, ch);
    return new String(array);
  }

  public static String fillString(int len, char ch, String param) {
    String p = (param != null) ? param : "";
    return (p + fillString(len, ch)).substring(0, len);
  }

  @Test
  public void test01_encrypt() {
    StackTracer.printCurrentTestMethod();
    String encrypted = XorUtil.encrypt(TXT, KEY, Charset.defaultCharset());
    StackTracer.printTestInfo(KEY, TXT, encrypted);
    assertTrue(!encrypted.equals(TXT));
  }

  @Test
  public void test02_decrypt() {
    StackTracer.printCurrentTestMethod();
    String encrypted = XorUtil.encrypt(TXT, KEY, Charset.defaultCharset());
    String decrypted = XorUtil.decrypt(encrypted, KEY, Charset.defaultCharset());
    StackTracer.printTestInfo(KEY, encrypted, decrypted);
    assertTrue(decrypted.equals(TXT));
  }

 @Test
  public void test03_encrypt() {
    StackTracer.printCurrentTestMethod();
    int maxLen = 0;
    for (String one : original) {
      if (one.length() > maxLen) {
        maxLen = one.length();
      }
    }

    for (int i = 0; i < original.length; i++) {
      encoded[i] = XorUtil.encrypt(original[i], KEY, Charset.defaultCharset());
      System.out.println(fillString(maxLen, ' ', original[i]) + "\t" + encoded[i]);
      assertTrue(!encoded.equals(original[i]));
    }
  }

  @Test
  public void test04_decrypt() {
    StackTracer.printCurrentTestMethod();
    int maxLen = 0;
    for (String one : encoded) {
      if (one.length() > maxLen) {
        maxLen = one.length();
      }
    }

    for (int i = 0; i < encoded.length; i++) {
      String decoded = XorUtil.decrypt(encoded[i], KEY, Charset.defaultCharset());
      System.out.println(fillString(maxLen, ' ', encoded[i]) + "\t" + decoded);
      assertTrue(decoded.equals(original[i]));
    }
  }


}