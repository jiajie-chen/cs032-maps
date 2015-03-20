package edu.brown.cs.is3.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.brown.cs.is3.trie.Trie;

/**
 * Generates suggestions based ona number of methods and options.
 * @author is3
 *
 */
public class Generator {
  private final Trie t;
  private final String word;
  private int led = 0;
  private boolean prefix = false;
  private boolean whitespace = false;

  /**
   * Constructs a basic generator.
   * @param word to autocorrect.
   * @param t Trie to use for generation.
   */
  public Generator(String word, Trie t) {
    this.t = t;
    this.word = word;
    return;
  }

  /**
   * Constructs an advanced generator.
   * @param word to autocorrect.
   * @param t Trie to use for generation.
   * @param led the maximum allow edit distance. 0 deactivates it.
   * @param prefix true if should use prefix suggestion, false otherwise.
   * @param whitespace true if should use whitespace suggestion, else false.
   */
  public Generator(
      String word, Trie t, int led, boolean prefix, boolean whitespace) {
    this.t = t;
    this.led = led;
    this.prefix = prefix;
    this.whitespace = whitespace;
    this.word = word;
  }

  /**
   * Sets led.
   * @param newLed maximum edit distance. 0 deactivates.
   * @return this
   */
  public Generator setLed(int newLed) {
    this.led = newLed;
    return this;
  }

  /**
   * Sets prefix.
   * @param newPre if should use prefix suggestion, false otherwise.
   * @return this.
   */
  public Generator setPrefix(boolean newPre) {
    this.prefix = newPre;
    return this;
  }

  /**
   * Sets whitespace.
   * @param newWhite true if should use whitespace suggestion, else false.
   * @return this.
   */
  public Generator setWhitespace(boolean newWhite) {
    this.whitespace = newWhite;
    return this;
  }

  /**
   * Generates a list of suggestions using the trie based on the options set.
   * @return list of suggestions based on the word and the options.
   */
  public List<String> generate() {
    Set<String> results = new HashSet<>();

    if (t == null) {
      return new ArrayList<>();
    }

    if (t.contains(word)) {
      results.add(word);
    }

    results.addAll(t.findLed(word, led));

    if (prefix) {
      results.addAll(t.findPrefixes(word));
    }

    if (whitespace) {
      results.addAll(t.whitespace(word));
    }

    List<String> toReturn = new ArrayList<>();
    toReturn.addAll(results);
    return toReturn;
  }
}
