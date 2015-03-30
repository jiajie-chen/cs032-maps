package edu.brown.cs.jc124.autocorrect.rank;

import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.jc124.util.Frequency;

/**
 * @author jchen
 *
 *         Implements the Frequency interface for usage in word frequencies
 */
public class NgramFrequency implements Frequency<String> {
  private Map<String, Integer> freq;
  private int total;

  /**
   * Makes an word empty frequency map.
   */
  public NgramFrequency() {
    freq = new HashMap<>();
    total = 0;
  }

  @Override
  public void addOccurrence(String item) {
    freq.put(item, freq.getOrDefault(item, 0) + 1);
    total++;
  }

  @Override
  public int getOccurrence(String item) {
    return freq.getOrDefault(item, 0);
  }

  @Override
  public int totalOccurrences() {
    return total;
  }

}
