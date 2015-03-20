package edu.brown.cs.is3.cartesian;

import java.util.Comparator;

/**
 * Comparator sorting based on distance between this and some fixed center
 * in ascending order of distance.
 */
public class CompareDistanceFrom implements Comparator<Cartesian> {
  private final Cartesian center;

  /**
   * Constructs a comparator about a center point.
   * @param center the point to which to compare.
   */
  public CompareDistanceFrom(Cartesian center) {
    this.center = center;
  }

  @Override
  public int compare(Cartesian p1, Cartesian p2) {
    if (center.getDistance(p2) < center.getDistance(p1)) {
      return 1;
    } else if (center.getDistance(p2) > center.getDistance(p1)) {
      return -1;
    } else {
      return 0;
    }
  }

}
