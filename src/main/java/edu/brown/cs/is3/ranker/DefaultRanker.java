package edu.brown.cs.is3.ranker;

import java.io.Serializable;
import java.util.Map;

/**
 * Implementation of ranker for comparing suggestions. Orders my exact match,
 * then bigram, then unigram, then alphabet.
 * @author is3
 *
 */
public class DefaultRanker implements Ranker, Serializable {
  /**
   * Generated serial number.
   */
  private static final long serialVersionUID = -6489869246719353942L;
  private final String word;
  private final String[] words;
  private final Map<String, Integer> unifreqs;
  private final Map<String, Integer> bifreqs;

  /**
   * Ranks search results as above.
   * @param word the last word in words.
   * @param words the thing the user is currently typing.
   * @param unifreqs mapping of unigrams to frequencies.
   * @param bifreqs mapping of bigrams to frequencies.
   */
  public DefaultRanker(String word, String[] words,
      Map<String, Integer> unifreqs, Map<String, Integer> bifreqs) {
    this.word = word;
    this.words = words;
    this.unifreqs = unifreqs;
    this.bifreqs = bifreqs;
  }

  @Override
  public int compare(String s1, String s2) {
    if (s1.equals(s2)) {
      return 0;
    }

    if (s1.equals(word)) {
      return -1;
    } else if (s2.equals(word)) {
      return 1;
    }

    if (words.length > 1) {
      String previous = words[words.length - 2];

      String s1Bi = previous + " " + s1.split("\\s+")[0];
      String s2Bi = previous + " " + s2.split("\\s+")[0];

      double firstBi = (double) bifreqs.getOrDefault(s1Bi, 0);
      double secondBi = (double) bifreqs.getOrDefault(s2Bi, 0);

      if (firstBi < secondBi) {
        return 1;
      } else if (firstBi > secondBi) {
        return -1;
      }
    }

    int s1Uni = unifreqs.getOrDefault(s1.split("\\s+")[0], 0);
    int s2Uni = unifreqs.getOrDefault(s2.split("\\s+")[0], 0);

    if (s1Uni < s2Uni) {
      return 1;
    } else if (s1Uni > s2Uni) {
      return -1;
    }

    return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
  }

}
