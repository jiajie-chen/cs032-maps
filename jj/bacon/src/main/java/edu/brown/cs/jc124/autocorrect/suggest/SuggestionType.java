package edu.brown.cs.jc124.autocorrect.suggest;

/**
 * @author jchen
 *
 *         The various types of suggestion a Suggestion can be, based on its
 *         Suggester
 */
public enum SuggestionType {
  LEVENSHTEIN, PREFIX, WHITESPACE, OTHER
}
