package edu.brown.cs.jc124.autocorrect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jchen
 *
 *         Contains utility methods to sanitize strings and send commands to an
 *         AutoCorrector.
 */
public final class AutoCorrectionParser {
  private AutoCorrectionParser() {
  }

  /**
   * Queries a given autocorrector with the input.
   *
   * @param auto
   *          the AutoCorrector object to use
   * @param in
   *          the input string to parse and pass to auto
   * @return the suggestions returned by the query
   */
  public static List<String> queryAutocorrect(AutoCorrector auto, String in) {
    // empty string or last character is whitespace
    if (in.isEmpty() || in.matches(".*[\\s]+$")) {
      return new ArrayList<String>();
    }

    List<String> words = new ArrayList<>();
    words.add(in);

    return auto.autocorrect(words);
  }
}
