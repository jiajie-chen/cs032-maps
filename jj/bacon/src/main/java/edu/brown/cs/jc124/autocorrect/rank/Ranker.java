package edu.brown.cs.jc124.autocorrect.rank;

import java.util.Comparator;

import edu.brown.cs.jc124.autocorrect.suggest.Suggester.Suggestion;

/**
 * @author jchen
 *
 *         Used for ranking suggestions by an arbitrary ranking.
 *
 * @param <T>
 *          the type of suggestion that it ranks by.
 */
public interface Ranker<T> {
  /**
   * Makes a comparator for ranking suggestions.
   *
   * @return the comparator for ranking.
   */
  Comparator<Suggestion<T>> suggestionRanker();
}
