package edu.brown.cs.jc124.kdtree;

import java.util.Arrays;

/**
 * @author jchen
 *
 *         Class that stores n-dimensional Cartesian coordinates
 */
public class CartesianCoordinate implements Coordinate {
  private int d;
  private double[] fields;

  /**
   * Creates a new Cartesian coordinate with a dimension and given fields.
   *
   * @param dimensions
   *          a positive integer that specifies how many fields/axes this object
   *          stores
   * @param fields
   *          the fields, in order by axis, of the coordinate
   * @throws DimensionMismatchException
   *           if the number of fields given deosn't match the dimensions
   * @throws IllegalArgumentException
   *           if the dimensions are not positive
   */
  public CartesianCoordinate(int dimensions, double... fields) {
    if (dimensions <= 0) {
      throw new IllegalArgumentException("dimensions must be positive integer");
    }
    if (dimensions != fields.length) {
      throw new DimensionMismatchException(
          "dimensions do not match number of fields");
    }

    d = dimensions;
    this.fields = Arrays.copyOf(fields, d);
  }

  /**
   * Makes a coordinate with all fields zeroed.
   *
   * @param dimensions
   *          a positive integer that specifies how many fields/axes this object
   *          stores
   * @throws DimensionMismatchException
   *           if the number of fields given deosn't match the dimensions
   * @throws IllegalArgumentException
   *           if the dimensions are not positive
   */
  public CartesianCoordinate(int dimensions) {
    this(dimensions, new double[dimensions]);
  }

  /**
   * Makes a coordinate with all fields zeroed and a dimension of 2.
   *
   * @throws DimensionMismatchException
   *           if the number of fields given deosn't match the dimensions
   * @throws IllegalArgumentException
   *           if the dimensions are not positive
   */
  public CartesianCoordinate() {
    this(2);
  }

  @Override
  public double getField(int axis) {
    if (axis < 0) {
      throw new IllegalArgumentException("axis must be non-negative integer");
    }
    if (axis >= d) {
      throw new IndexOutOfBoundsException(
          "axis is out of bounds of dimensions");
    }

    return fields[axis];
  }

  @Override
  public double squaredDistance(Coordinate c) {
    if (getDimensions() != c.getDimensions()) {
      throw new DimensionMismatchException(
          "passed coordinate does not match dimensions");
    }

    double sqDist = 0;
    for (int i = 0; i < d; i++) {
      sqDist += Math.pow(fieldDistance(c, i), 2);
    }
    return sqDist;
  }

  @Override
  public int getDimensions() {
    return d;
  }
}
