package edu.brown.cs.jc124.autocorrect.rank;

import java.util.Comparator;
import java.util.List;

import edu.brown.cs.jc124.autocorrect.suggest.Suggester.Suggestion;

/**
 * @author jchen
 *
 *         Implements a Ranker using that ranks suggestions in order by exact
 *         matches, bigram frequency, unigram frequency, then alphabetical.
 */
public class BasicRanker implements Ranker<String> {
  private NgramFrequency bigram;
  private NgramFrequency unigram;

  /**
   * Makes a BasicRanker with the given word frequencies.
   *
   * @param bigram
   *          the bigram frequencies to use for ranking
   * @param unigram
   *          the unigram frequencies to use for ranking
   */
  public BasicRanker(NgramFrequency bigram, NgramFrequency unigram) {
    this.bigram = bigram;
    this.unigram = unigram;
  }

  @Override
  public Comparator<Suggestion<String>> suggestionRanker() {
    return new Comparator<Suggestion<String>>() {

      private boolean exact(Suggestion<String> s) {
        String suggested = s.item();
        List<String> input = s.input();

        return !input.isEmpty()
            && suggested.equals(input.get(input.size() - 1));
      }

      private int bigramFreq(Suggestion<String> s) {
        String suggested = s.item();
        List<String> input = s.input();

        if (input.size() < 2) {
          return 0;
        }

        String prev = input.get(input.size() - 2);
        String head = suggested.split("[^A-Za-z]+")[0];

        return bigram.getOccurrence(prev + " " + head);
      }

      private int unigramFreq(Suggestion<String> s) {
        return unigram.getOccurrence(s.item());
      }

      @Override
      public int compare(Suggestion<String> s1, Suggestion<String> s2) {
        if (exact(s1)) {
          return -1;
        }

        if (exact(s2)) {
          return 1;
        }

        int b1 = bigramFreq(s1);
        int b2 = bigramFreq(s2);
        if (b1 != b2) {
          // return -1 if b1 is more frequent, 1 if b2 is more frequent
          return (int) Math.signum(b2 - b1);
        }

        int u1 = unigramFreq(s1);
        int u2 = unigramFreq(s2);
        if (u1 != u2) {
          // return -1 if u1 is more frequent, 1 if u2 is more frequent
          return (int) Math.signum(u2 - u1);
        }

        return s1.item().compareTo(s2.item());
      }

    };
  }
}
