package edu.brown.cs.jc124.autocorrect.suggest;

import java.util.Collections;
import java.util.List;

import edu.brown.cs.jc124.autocorrect.suggest.Suggester.Suggestion;

/**
 * @author jchen
 *
 *         Factory for creating suggestions of strings.
 */
public class SuggestionFactory {
  private static class BasicStringSuggestion implements Suggestion<String> {
    private String suggest;
    private double weight;
    private List<String> input;
    private SuggestionType type;

    private BasicStringSuggestion(List<String> input, String suggest,
        double weight, SuggestionType type) {
      this.input = Collections.unmodifiableList(input);
      this.suggest = suggest;
      this.weight = weight;
      this.type = type;
    }

    @Override
    public String item() {
      return suggest;
    }

    @Override
    public List<String> input() {
      return input;
    }

    @Override
    public double weight() {
      return weight;
    }

    @Override
    public SuggestionType type() {
      return type;
    }
  }

  /**
   * Makes a suggestion for correction.
   *
   * @param input
   *          the input used to create the suggestion
   * @param suggest
   *          the actual suggested correction
   * @param weight
   *          the weight of the suggestion
   * @param type
   *          the type of suggestion it is
   *
   * @return the suggestion of the given parameters
   */
  public static Suggestion<String> create(List<String> input, String suggest,
      double weight, SuggestionType type) {
    return new BasicStringSuggestion(input, suggest, weight, type);
  }

  /**
   * Makes a suggestion for Levenshtein corrections.
   *
   * @param input
   *          the input used to create the suggestion
   * @param suggest
   *          the actual suggested correction
   * @param weight
   *          the weight of the suggestion
   *
   * @return the Levenshtein suggestion of the given parameters
   */
  public static Suggestion<String> createLevenshtein(List<String> input,
      String suggest, double weight) {
    return new BasicStringSuggestion(input, suggest, weight,
        SuggestionType.LEVENSHTEIN);
  }

  /**
   * Makes a suggestion for prefix corrections.
   *
   * @param input
   *          the input used to create the suggestion
   * @param suggest
   *          the actual suggested correction
   * @param weight
   *          the weight of the suggestion
   *
   * @return the prefix suggestion of the given parameters
   */
  public static Suggestion<String> createPrefix(List<String> input,
      String suggest, double weight) {
    return new BasicStringSuggestion(input, suggest, weight,
        SuggestionType.PREFIX);
  }

  /**
   * Makes a suggestion for whitespace corrections.
   *
   * @param input
   *          the input used to create the suggestion
   * @param suggest
   *          the actual suggested correction
   * @param weight
   *          the weight of the suggestion
   *
   * @return the whitespace suggestion of the given parameters
   */
  public static Suggestion<String> createWhitespace(List<String> input,
      String suggest, double weight) {
    return new BasicStringSuggestion(input, suggest, weight,
        SuggestionType.WHITESPACE);
  }
}
