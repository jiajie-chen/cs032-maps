package edu.brown.cs.jc124.autocorrect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.brown.cs.jc124.autocorrect.rank.Ranker;
import edu.brown.cs.jc124.autocorrect.rank.SmartRanker;
import edu.brown.cs.jc124.autocorrect.suggest.LevenshteinSuggester;
import edu.brown.cs.jc124.autocorrect.suggest.PrefixSuggester;
import edu.brown.cs.jc124.autocorrect.suggest.Suggester;
import edu.brown.cs.jc124.autocorrect.suggest.Suggester.Suggestion;
import edu.brown.cs.jc124.autocorrect.suggest.WhitespaceSuggester;
import edu.brown.cs.jc124.data.Trie;

/**
 * @author jchen
 *
 *         Contains the methods to get autocorrections, from input strings.
 */
public final class AutoCorrector {
  /**
   * @author jchen
   *
   *         The builder for creating a new AutoCorrector.
   */
  public static class Builder {
    private AutoCorrector auto;

    /**
     * Makes a new builder to began AutoCorrector creation.
     */
    public Builder() {
      auto = new AutoCorrector();
      auto.suggesters = new ArrayList<>();
    }

    /**
     * Creates a new AutoCorrector based on the builder's settings.
     *
     * @return the new AutoCorrector
     */
    public AutoCorrector build() {
      if (auto.suggesters.size() <= 0) {
        throw new IllegalArgumentException("no suggesters were provided");
      }
      if (auto.ranker == null) {
        throw new NullPointerException("no ranker was provided");
      }

      return auto;
    }

    /**
     * Adds a new Suggester object to the AutoCorrector.
     *
     * @param s
     *          the suggester to add
     * @return the current Builder object, for chaining commands
     */
    public Builder addSuggester(Suggester<String> s) {
      auto.suggesters.add(s);
      return this;
    }

    /**
     * Sets the Ranker object to the AutoCorrector.
     *
     * @param r
     *          the ranker for the AutoCorrector to use
     * @return the current Builder object, for chaining commands
     */
    public Builder setRanker(Ranker<String> r) {
      auto.ranker = r;
      return this;
    }

    /**
     * Helper for adding a Levenshtein suggester.
     *
     * @param trie
     *          the trie for the suggester to perform suggestions with
     * @param distance
     *          the max edit distance for the suggester to look for
     * @return the current Builder object, for chaining commands
     */
    public Builder addLevenshteinSuggester(Trie<String> trie, int distance) {
      return addSuggester(new LevenshteinSuggester(trie, distance));
    }

    /**
     * Helper for adding a prefix suggester.
     *
     * @param trie
     *          the trie for the suggester to perform suggestions with
     * @return the current Builder object, for chaining commands
     */
    public Builder addPrefixSuggester(Trie<String> trie) {
      return addSuggester(new PrefixSuggester(trie));
    }

    /**
     * Helper for adding a whitespace suggester.
     *
     * @param trie
     *          the trie for the suggester to perform suggestions with
     * @return the current Builder object, for chaining commands
     */
    public Builder addWhitespaceSuggester(Trie<String> trie) {
      return addSuggester(new WhitespaceSuggester(trie));
    }

    /**
     * Helper for setting the ranker to a smart ranker.
     *
     * @return the current Builder object, for chaining commands
     */
    public Builder setSmartRanker() {
      return setRanker(new SmartRanker());
    }
  }

  private List<Suggester<String>> suggesters;
  private Ranker<String> ranker;

  // disable direct creation
  private AutoCorrector() {
  }

  /**
   * Performs auto correction on a sentence, using its suggesters and ranking
   * with its ranker.
   *
   * @param words
   *          the sentence to correct, as a list of its words
   * @return the set of strings given by the suggesters, ordered by the ranker
   */
  public List<String> autocorrect(List<String> words) {
    List<Suggestion<String>> sug = getSuggestions(words);
    List<String> results = sortAutoCorrections(sug);

    // must remove duplicate string suggestions, preserve order
    List<String> sugSet = new ArrayList<>();
    for (String s : results) {
      if (!sugSet.contains(s)) {
        sugSet.add(s);
      }
    }

    return sugSet;
  }

  // gets all suggestions from the suggesters
  private List<Suggestion<String>> getSuggestions(List<String> words) {
    List<Suggestion<String>> suggests = new ArrayList<>();
    for (Suggester<String> s : suggesters) {
      suggests.addAll(s.suggestions(words));
    }

    return suggests;
  }

  // sorts all returned ranks by their rankings, and gives the strings
  private List<String> sortAutoCorrections(List<Suggestion<String>> sug) {
    List<Suggestion<String>> sorted = new ArrayList<>(sug);

    Collections.sort(sorted, ranker.suggestionRanker());

    List<String> results = new ArrayList<>();
    for (Suggestion<String> s : sorted) {
      results.add(s.suggest());
    }

    return results;
  }
}
