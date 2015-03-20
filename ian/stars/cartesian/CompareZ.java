package edu.brown.cs.is3.cartesian;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator sorting purely along the z axis.
 */
public class CompareZ implements Comparator<Cartesian>, Serializable {

  /**
   * Automatically generated serialization.
   */
  private static final long serialVersionUID = 254430781710309349L;

  @Override
  public int compare(Cartesian p1, Cartesian p2) {
    if (p1 == null && p2 == null) {
      return 0;
    } else if (p1 == null) {
      return 1;
    } else if (p2 ==  null) {
      return -1;
    }

    if (p2.getZ() > p1.getZ()) {
      return 1;
    } else if (p2.getZ() < p1.getZ()) {
      return -1;
    } else {
      return 0;
    }
  }
}
