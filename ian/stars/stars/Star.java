package edu.brown.cs.is3.stars;

import edu.brown.cs.is3.cartesian.Cartesian;
import edu.brown.cs.is3.cartesian.Position;

/**
 * Class representing a star coordinate based on its ID, name, and position.
 */
public class Star implements Cartesian {

  private final int id;
  private final String name;
  private final Position pos;
  private static final double HASHING_PRIME = 19;

  /**
   * Constructs a star from an array of strings (useful for rls
   * eading from
   * input files).
   * @param fields an array of strings extracted from a suitable CSV.
   */
  public Star(String[] fields) {
    this.id = Integer.parseInt(fields[Main.ID_FIELD]);
    this.name = fields[Main.NAME_FIELD];

    double x;
    double y;
    double z;

    try {
      x = Double.parseDouble(fields[Main.X_FIELD]);
      y = Double.parseDouble(fields[Main.Y_FIELD]);
      z = Double.parseDouble(fields[Main.Z_FIELD]);
    } catch (NumberFormatException e) {
      System.out.println("ERROR: Input file corrupted. "
          + "Stars must be made of valid doubles.");
      throw new NumberFormatException();
    }

    this.pos = new Position(x, y, z);
  }

  /**
   * Constructs a star purely out of it's five field components.
   * @param id the integer ID of the star.
   * @param name the name of the star.
   * @param x coordinate.
   * @param y coordinate.
   * @param z coordinate.
   */
  public Star(int id, String name, int x, int y, int z) {
    this.id = id;
    this.name = name;
    this.pos = new Position(x, y, z);
  }

  /**
   * Constructs a star out of a Position object and its other two fields.
   * @param id the integer ID of the star.
   * @param name name the name of the star.
   * @param pos a Position object representing the star.
   */
  public Star(int id, String name, Position pos) {
    this.id = id;
    this.name = name;
    this.pos = pos;
  }

  /**
   * Returns the position of the star.
   * @return position of the star.
   */
  public Position getPos() {
    return pos;
  }

  @Override
  public double getX() {
    return pos.getX();
  }

  @Override
  public double getY() {
    return pos.getY();
  }

  @Override
  public double getZ() {
    return pos.getZ();
  }

  /**
   * Returns the name of the star.
   * @return the name of the star.
   */
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Star)) {
      return false;
    }

    Star str = (Star) o;

    return this.id == str.id
        && this.name.equals(str.getName())
        && this.pos.equals(str.getPos());
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
    return "" + this.id;
  }

}
