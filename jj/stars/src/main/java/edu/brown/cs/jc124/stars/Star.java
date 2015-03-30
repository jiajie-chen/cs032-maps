package edu.brown.cs.jc124.stars;

import edu.brown.cs.jc124.kdtree.CartesianCoordinate;

/**
 * @author jchen
 *
 *         An extension of CartesianCoordinate for additionally holding
 *         star-specific data.
 */
public class Star extends CartesianCoordinate {
  private int starId;
  private String name;

  /**
   * Makes a new star with a name and id at the given x, y, z coordinates.
   *
   * @param starId
   *          the star's id
   * @param name
   *          the name of the star
   * @param x
   *          the x coordinate of its location
   * @param y
   *          the y coordinate of its location
   * @param z
   *          the z coordinate of its location
   */
  public Star(int starId, String name, double x, double y, double z) {
    super(3, x, y, z);
    this.starId = starId;
    this.name = name;
  }

  /**
   * Gets the star's id.
   *
   * @return the star's id
   */
  public int getId() {
    return starId;
  }

  /**
   * Gets the star's name.
   *
   * @return the star's name
   */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "[" + name + ", " + starId + ", <" + getField(0) + ", "
        + getField(1) + ", " + getField(2) + ">]";
  }
}
