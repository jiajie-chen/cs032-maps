package edu.brown.cs.is3.cartesian;

/**
 * Interface specifying the bare requirements for an object to be added to a three
 * dimensional KDTree
 */
public interface Cartesian {
  /**
   * Calculates the Euclidean distance between this and pos.
   * @param pos the other Cartesian to compare to.
   * @return the distance between the two Cartesian.
   */
  default double getDistance(Cartesian pos) {
    double x = pos.getX() - this.getX();
    double y = pos.getY() - this.getY();
    double z = pos.getZ() - this.getZ();
    return Math.sqrt((x*x) + (y*y) + (z*z));
  }

  /**
   * Calculates the distance between this and some other position along one axis.
   * @param pos the other Cartesian to compare to.
   * @param depth the current depth of the tree being examined.
   * @return the one dimensional distance between the two points.
   */
  default double getDimensionalDistance(Cartesian pos, int depth) {
    if (depth < 0) {
      System.out.println("ERROR: Dimension must be non-negative.");
      throw new IllegalArgumentException();
    }

    int dimension = depth % 3;

    if (dimension == 0) {
      return Math.abs(this.getX() - pos.getX());
    } else if (dimension == 1) {
      return Math.abs(this.getY() - pos.getY());
    } else if (dimension == 2) {
      return Math.abs(this.getZ() - pos.getZ());
    } else {
      return 0;
    }
  }

  /**
   * Return the x value of the Cartesian.
   * @return x value.
   */
  double getX();

  /**
   * Return the y value of the Cartesian.
   * @return y value.
   */
  double getY();

  /**
   * Return the z value of the Cartesian.
   * @return z value.
   */
  double getZ();
}
