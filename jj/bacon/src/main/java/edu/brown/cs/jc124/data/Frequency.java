package edu.brown.cs.jc124.data;

/**
 * @author jchen
 *
 *         Interface for mapping frequencies and occurrences of objects.
 *
 * @param <T>
 *          the objects to count occurrences for
 */
public interface Frequency<T> {
  /**
   * Increments the occurrence for that item.
   *
   * @param item
   *          the item to store
   */
  void addOccurrence(T item);

  /**
   * Gets the current amount of frequencies for that item.
   *
   * @param item
   *          the item to look for
   * @return the occurrences of that item
   */
  int getOccurrence(T item);

  /**
   * Get the total number of occurrences of all the items.
   *
   * @return the total number of occurrences
   */
  int totalOccurrences();

  /**
   * Get the frequency, from 0 to 1, of the given item.
   *
   * @param item
   *          the item to look for
   * @return the frequency of that item
   */
  default double getFrequency(T item) {
    return ((double) getOccurrence(item)) / totalOccurrences();
  }
}
