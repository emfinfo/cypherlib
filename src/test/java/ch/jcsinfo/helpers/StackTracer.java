package ch.jcsinfo.helpers;

/**
 * Permet de récupérer des informations telles que la classe ou la méthode courante (très
 * utile pour les messages de debug).
 *
 * @author Jean-Claude Stritt
 */
public class StackTracer {

  private static String trace(StackTraceElement e[], int level, int which) {
    String answer = "";
    if (e != null && e.length >= level) {
      StackTraceElement s = e[level];
      if (s != null) {
        switch (which) {
          case 1:
            answer = s.getClassName();
            break;
          case 2:
            answer = s.getMethodName();
            break;
          case 3:
            answer = s.getMethodName() + "... (" + s.getClassName() + ")";
            break;
          case 4:
            answer = s.getClassName() + " (" + s.getMethodName() + "): ";
        }
      }
    }
    return answer;
  }

  public static String getCurrentClass() {
    String[] cl = trace(Thread.currentThread().getStackTrace(), 3, 1).split("\\.");
    return cl[cl.length-1];
  }

  public static String getCurrentMethod() {
    return trace(Thread.currentThread().getStackTrace(), 3, 2);
  }

  public static void printCurrentTestMethod(String info) {
    System.out.println("\n*** " + getCurrentClass() + " - " + getCurrentMethod() + info);
  }

  public static void printCurrentTestMethod() {
    System.out.println("\n*** " + getCurrentClass() + " - " + getCurrentMethod());
  }

  public static void printTestInfo(String source, String result) {
    System.out.println("  - source: " + source);
    System.out.println("  - result: " + result);
  }

  public static void printTestInfo(String key, String source, String result) {
    System.out.println("  - key:    " + key);
    System.out.println("  - source: " + source);
    System.out.println("  - result: " + result);
  }
  
  public static void printSimpleInfo(String label, String info) {
    System.out.println("  - " + label + ": " + info);
  }
  

}
