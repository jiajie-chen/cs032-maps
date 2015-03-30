package edu.brown.cs.jc124.autocorrect.rank;

import java.util.Comparator;
import java.util.List;

import edu.brown.cs.jc124.autocorrect.suggest.Suggester.Suggestion;

/**
 * @author jchen
 *
 *         Implements a smart way to rank string suggestions by weighted
 *         categories.
 */
public class SmartRanker implements Ranker<String> {
  // weights of frequency (more frequent/larger is better)
  private static final double EXACT_WEIGHT = Double.POSITIVE_INFINITY;
  private static final double BIGRAM_WEIGHT = 100;
  private static final double UNIGRAM_WEIGHT = 10;
  private static final double ALPHA_WEIGHT = 0.1;
  // weights of suggestions (since these weights generally correspond
  // to minimum amount of changes, better is smaller)
  private static final double LEVENSHTEIN_WEIGHT = 50;
  private static final double PREFIX_WEIGHT = 10;
  private static final double WHITESPACE_WEIGHT = 1;

  private NgramFrequency bigram;
  private NgramFrequency unigram;

  /**
   * Makes a SmartRanker with the given word frequencies.
   *
   * @param bigram
   *          the bigram frequencies to use for ranking
   * @param unigram
   *          the unigram frequencies to use for ranking
   */
  public SmartRanker(NgramFrequency bigram, NgramFrequency unigram) {
    this.bigram = bigram;
    this.unigram = unigram;
  }

  @Override
  public Comparator<Suggestion<String>> suggestionRanker() {
    return new Comparator<Suggestion<String>>() {
      private double calculateWeight(Suggestion<String> s) {
        String suggested = s.item();
        List<String> input = s.input();

        double weight = 0.0;

        // empty input
        if (input.isEmpty()) {
          return Double.NEGATIVE_INFINITY;
        }

        if (suggested.equals(input.get(input.size() - 1))) {
          // direct match
          return EXACT_WEIGHT;
        }

        if (input.size() > 1) {
          // bigram probability
          String prev = input.get(input.size() - 2);
          String head = suggested.split("[^A-Za-z]+")[0];
          weight += bigram.getOccurrence(prev + " " + head) * BIGRAM_WEIGHT;
        }
        // unigram probability
        weight += unigram.getFrequency(suggested) * UNIGRAM_WEIGHT;
        // alphabetical (only first character)
        weight += ((double) 'z') / (suggested.charAt(0) - 'a' + 1)
            * ALPHA_WEIGHT;

        // subtract these weights, since smaller is better for these
        switch (s.type()) {
          case LEVENSHTEIN:
            weight -= s.weight() * LEVENSHTEIN_WEIGHT;
            break;

          case PREFIX:
            weight -= s.weight() * PREFIX_WEIGHT;
            break;

          case WHITESPACE:
            weight -= s.weight() * WHITESPACE_WEIGHT;
            break;

          case OTHER:
          default:
            break;
        }

        return weight;
      }

      @Override
      public int compare(Suggestion<String> s1, Suggestion<String> s2) {
        double w1 = calculateWeight(s1);
        double w2 = calculateWeight(s2);

        // higher weights is higher on list (less than)
        return (int) Math.signum(w2 - w1);
      }

    };
  }
}
