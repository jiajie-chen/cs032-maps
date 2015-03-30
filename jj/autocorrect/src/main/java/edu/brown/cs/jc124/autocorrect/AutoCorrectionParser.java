package edu.brown.cs.jc124.autocorrect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    List<String> words = cleanPhrase(in);

    return auto.autocorrect(words);
  }

  /**
   * Formats an input string to make it valid for use by the AutoCorrector.
   * Constructs a list of words of the string, without uppercase, punctuation,
   * or whitespace
   *
   * @param in
   *          the string to clean
   * @return the cleaned list of words in the string
   */
  public static List<String> cleanPhrase(String in) {
    // replace all non-letter characters with spaces
    String escaped = in.replaceAll("[^A-Za-z]+", " ");
    // trim and lowercase
    escaped = escaped.trim().toLowerCase();
    // split by white space/ non-letter characters
    return Collections.unmodifiableList(Arrays.asList(escaped
        .split("[^A-Za-z]+")));
  }

}
