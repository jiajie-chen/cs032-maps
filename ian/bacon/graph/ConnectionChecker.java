package edu.brown.cs.is3.graph;

/**
 * Interface to check if connection between nodes is valid in bacon.
 * @author is3
 *
 * @param <E> generic type of connection checker.
 */
public interface ConnectionChecker<E> {

  /**
   * Checks if a given edge is valid.
   * @param edge to examine.
   * @return true if so and false otherwise.
   */
  boolean validConnection(Edge<E> edge);
}
