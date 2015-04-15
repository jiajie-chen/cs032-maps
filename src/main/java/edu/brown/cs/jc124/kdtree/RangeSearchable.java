package edu.brown.cs.jc124.kdtree;

import java.util.List;

/**
 * @author jchen
 *
 *         an interface for creating collections that can execute searches over
 *         ranges of items.
 *
 * @param <T> the type of items to be searched over
 */
public interface RangeSearchable<T> {
  /**
   * Gets the k-nearest neighbors from the point (includes the point if it in
   * the collection).
   *
   * @param point the coordinate to look around of
   * @param k the number of neighbors to find
   * @return the list of size k that has the k-nearest neighbors
   */
  List<T> kNearestNeighbors(T point, int k);

  /**
   * Finds all neighbors of a point within a certain distance from it.
   *
   * @param point the coordinate to look around of
   * @param distance the range to look over, inclusive
   * @return the list of all coordinates within that range
   */
  List<T> withinDistance(T point, double distance);
}
