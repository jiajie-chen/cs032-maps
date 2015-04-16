package edu.brown.cs.jc124.kdtree;

/**
 * @author jchen
 *
 *         An interface for implementing Coordinates with certain dimensions and
 *         distances from other coordinates.
 */
public interface Coordinate {
  /**
   * Gets the dimensionality that this coordinate stores.
   *
   * @return coordinate's dimension
   */
  public int getDimensions();

  /**
   * Gets a certain field of the coordinate.
   *
   * @param axis the axis of the field, zero-indexed
   * @return the field at that axis
   * @throws IllegalArgumentException if the axis is negative
   * @throws IndexOutOfBoundsException if the axis exceeds the dimensionality of
   *         the coordinate
   */
  public double getField(int axis);

  /**
   * Gets the squared distance for this coordinate to another. Used by KdTree
   * for efficiency.
   *
   * @param c the coordinate to measure distance to
   * @return the squared distance of the coordinate from c
   * @throws DimensionMismatchException if the given coordinate doesn't match
   *         dimensionality with the given coordinate
   */
  public double squaredDistance(Coordinate c);

  /**
   * Gets the distance for this coordinate to another
   *
   * @param c the coordinate to measure distance to
   * @return the squared distance of the coordinate from c
   * @throws DimensionMismatchException if the given coordinate doesn't match
   *         dimensionality with the given coordinate
   */
  public default double distance(Coordinate c) {
    return Math.sqrt(squaredDistance(c));
  }

  /**
   * gets the distance(difference) from one of this coordinate's field to
   * another coordinate's respective field.
   *
   * @param c the other coordinate
   * @param axis the axis to compare
   * @return the difference between the two coordinates on that axis
   * @throws IllegalArgumentException if the axis is negative
   * @throws DimensionMismatchException if the given coordinate doesn't match
   *         dimensionality with the given coordinate
   * @throws IndexOutOfBoundsException if the axis exceeds the dimensionality of
   *         the coordinate
   */
  public default double fieldDistance(Coordinate c, int axis) {
    if (getDimensions() != c.getDimensions()) {
      throw new DimensionMismatchException(
          "passed coordinate does not match dimensions");
    }
    return getField(axis) - c.getField(axis);
  }
}
