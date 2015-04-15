package edu.brown.cs.is3.ranker;

import java.io.Serializable;
import java.util.Map;

/**
 * Ranks the results based on the product of the unigram score and the bigram
 * score divided by the led and previous unigram score. Uses alphabetical order
 * as a tiebreaker. Gives a special bonus to prefixes.
 * @author is3
 *
 */
public class SmartRanker implements Ranker, Serializable {
  /**
   * Generated serial number.
   */
  private static final long serialVersionUID = -3359907458042857862L;
  private final String word;
  private final String[] words;
  private final Map<String, Integer> unifreqs;
  private final Map<String, Integer> bifreqs;
  private static final int PREFIX_BONUS = 8;

  /**
   * Ranks search results as above.
   * @param word the last word in words.
   * @param words the thing the user is currently typing.
   * @param unifreqs mapping of unigrams to frequencies.
   * @param bifreqs mapping of bigrams to frequencies.
   */
  public SmartRanker(String word, String[] words,
      Map<String, Integer> unifreqs, Map<String, Integer> bifreqs) {
    this.word = word;
    this.words = words;
    this.unifreqs = unifreqs;
    this.bifreqs = bifreqs;
  }

  @Override
  public int compare(String s1, String s2) {
    int led1 = Math.max(editDistance(s1, word), 1);
    int led2 = Math.max(editDistance(s2, word), 1);

    int prefix1 = s1.startsWith(word) ? PREFIX_BONUS : 1;
    int prefix2 = s1.startsWith(word) ? PREFIX_BONUS : 1;

    int firstBi = 1;
    int secondBi = 1;
    double prevUni = 1;

    if (words.length > 1) {
      String previous = words[words.length - 2];
      prevUni = unifreqs.getOrDefault(previous.split("\\s+")[0], 1);

      String s1Bi = previous + " " + s1.split("\\s+")[0];
      String s2Bi = previous + " " + s2.split("\\s+")[0];

      firstBi = bifreqs.getOrDefault(s1Bi, 1);
      secondBi = bifreqs.getOrDefault(s2Bi, 1);
    }

    int s1Uni = unifreqs.getOrDefault(s1.split("\\s+")[0], 0);
    int s2Uni = unifreqs.getOrDefault(s2.split("\\s+")[0], 0);

    double total1 = s1Uni * (firstBi / prevUni) / led1 * prefix1;
    double total2 = s2Uni * (secondBi / prevUni) / led2 * prefix2;

    if (total1 < total2) {
      return 1;
    } else if (total1 > total2) {
      return -1;
    }

    return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
  }

  /**
   * Calculates the LED between two strings.
   * @param s1 first string.
   * @param s2 second string.
   * @return the LED between the two strings.
   */
  public static int editDistance(String s1, String s2) {
    if (s1 == null && s2 == null) {
      return 0;
    } else if (s1 == null || s1.equals("")) {
      return s2.length();
    } else if (s2 == null || s2.equals("")) {
      return s1.length();
    } else if (s1.equals(s2)) {
      return 0;
    }

    int len1 = s1.length() + 1;
    int len2 = s2.length() + 1;

    int[] oldDist = new int[len1];
    int[] newDist = new int[len1];

    for (int i = 0; i < len1; i++) {
      oldDist[i] = i;
    }

    for (int j = 1; j < len2; j++) {
      newDist[0] = j;

      for (int i = 1; i < len1; i++) {
        int match = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;

        int replace = oldDist[i - 1] + match;
        int insert = oldDist[i] + 1;
        int delete = newDist[i - 1] + 1;

        newDist[i] = minimum(replace, insert, delete);
      }

      int[] tmp = oldDist;
      oldDist = newDist;
      newDist = tmp;
    }

    return oldDist[len1 - 1];
  }

  /**
   * Finds the minimum of three numbers.
   * @param i first number.
   * @param j second number.
   * @param k third number.
   * @return min of i, j, and k.
   */
  private static int minimum(int i, int j, int k) {
    return Math.min(Math.min(i, j), k);
  }
}
