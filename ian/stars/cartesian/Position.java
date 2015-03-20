package edu.brown.cs.is3.cartesian;

/**
 * Standard three dimensional implementation of Cartesian including
 * x, y, and z coordinates.
 */
public class Position implements Cartesian {
  private final double x;
  private final double y;
  private final double z;
  private static final double HASHING_PRIME = 17;

  /**
   * Builds a position out of three coordinates.
   * @param x coordinate.
   * @param y coordinate.
   * @param z coordinate.
   */
  public Position(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public double getX() {
    return x;
  }

  @Override
  public double getY() {
    return y;
  }

  @Override
  public double getZ() {
    return z;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Position)) {
      return false;
    }

    Position pos = (Position) o;

    return this.x == pos.getX() && this.y == pos.getY() && this.z == pos.getZ();
  }

  @Override
  public int hashCode() {
    double hash = HASHING_PRIME;
    hash *= this.getX();
    hash += this.getY();
    hash = Math.pow(hash, this.getZ());

    return (int) hash;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ", " + z + ")";
  }
}
