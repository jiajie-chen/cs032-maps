package edu.brown.cs.jc124.autocorrect.suggest;

import java.util.List;

/**
 * @author jchen
 *
 *         Interface for creating suggestor that provides suggestions.
 *
 * @param <T>
 *          the type of suggestion to provide
 */
public interface Suggester<T> {
  /**
   * @author jchen
   *
   *         Interface for defining a suggestion, with associated values.
   *
   * @param <T>
   *          the type of the suggested item
   */
  interface Suggestion<T> {
    /**
     * Gives the input given to Suggester used to create the suggestion
     *
     * @return the input used to create the suggestion
     */
    List<T> input();

    /**
     * Gives the suggested object that this holds.
     *
     * @return the suggestion that this holds
     */
    T item();

    /**
     * Gives the weight/value of the suggestion.
     *
     * @return the weight/value of the suggestion
     */
    double weight();

    /**
     * Gives the type of suggestion this is.
     *
     * @return the type of suggestion this is
     */
    SuggestionType type();

    /**
     * Creates the string representation of the suggestion.
     *
     * @return the string representation of the suggestion
     */
    default String suggest() {
      T suggested = item();
      List<T> input = input();

      String phrase = "";
      for (int i = 0; i < input.size() - 1; i++) {
        phrase += input.get(i).toString() + " ";
      }
      phrase += suggested.toString();

      return phrase;
    }
  }

  /**
   * Creates a list of suggestions for the given input
   *
   * @param input
   *          the input phrase to look up suggestions for
   * @return the suggestions found for that input
   */
  List<Suggestion<T>> suggestions(List<T> input);
}
