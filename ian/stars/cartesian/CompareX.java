package edu.brown.cs.is3.cartesian;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator sorting purely along the x axis.
 */
public class CompareX implements Comparator<Cartesian>, Serializable {

  /**
   * Automatically generated serialization.
   */
  private static final long serialVersionUID = 5038574124705174109L;

  @Override
  public int compare(Cartesian p1, Cartesian p2) {
    if (p1 == null && p2 == null) {
      return 0;
    } else if (p1 == null) {
      return 1;
    } else if (p2 ==  null) {
      return -1;
    }

    if (p2.getX() > p1.getX()) {
      return 1;
    } else if (p2.getX() < p1.getX()) {
      return -1;
    } else {
      return 0;
    }
  }

}
