package edu.brown.cs.is3.cartesian;

import edu.brown.cs.jc124.kdtree.Coordinate;
import edu.brown.cs.jc124.kdtree.DimensionMismatchException;

/**
 * @author jchen
 *
 *         Class that stores 2-dimensional latitude and longitudes, as radians.
 *         Distances are in miles.
 */
public class RadianLatLng implements Coordinate {
  // probably will never change, but good convention I guess
  public static final int LATLNG_DIM = 2;
  public static final int LAT_AXIS = 0;
  public static final int LNG_AXIS = 1;
  // earth's radius, for distances (in mile)
  private static final int EARTH_RADIUS = 6371000;

  public static final double MAX_LAT = 90;
  public static final double MAX_LNG = 180;

  private final double lat, lng;

  /**
   * Creates a new LatLng point with the given fields.
   *
   * @param lat the latitude of the point, in radians
   * @param lng the longitude of the point, in radians
   */
  public RadianLatLng(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  /**
   * Makes a point with lat and lng zeroed.
   */
  public RadianLatLng() {
    this(0, 0);
  }

  @Override
  public double getField(int axis) {
    if (axis < 0) {
      throw new IllegalArgumentException("axis must be non-negative integer");
    }

    switch (axis) {
      case LAT_AXIS:
        return lat;

      case LNG_AXIS:
        return lng;

      default:
        throw new IndexOutOfBoundsException(
            "axis is out of bounds of dimensions");
    }
  }

  /**
   * Gets the latitude of this point.
   * @return the latitude in radians
   */
  public double getLat() {
    return getField(LAT_AXIS);
  }

  /**
   * Gets the longitude of this point.
   * @return the longitude in radians
   */
  public double getLng() {
    return getField(LNG_AXIS);
  }

  @Override
  public double distance(Coordinate c) {
    if (c instanceof RadianLatLng) {
      return haversine((RadianLatLng) c);
    } else {
      throw new DimensionMismatchException(
          "this RadianLatLng can only get distance to another RadianLatLng");
    }
  }

  @Override
  public double squaredDistance(Coordinate c) {
    return Math.pow(distance(c), 2);
  }

  // haversine formula for great circle distance
  private double haversine(RadianLatLng l) {
    // phi is lat, lambda is lng
    double p1 = getLat(); // phi 1
    double p2 = l.getLat(); // phi 2
    double dP = p2 - p1; // delta of phi
    double dL = l.getLng() - getLng(); // delta of lambda

    // a = sin^2(dP/2) + cos(p1) * cos(p2) * sin^2(dL/2)
    double a =
        Math.pow(Math.sin(dP / 2), 2)
            + Math.cos(p1) * Math.cos(p2)
            * Math.pow(Math.sin(dL / 2), 2);
    // c = 2 * atan2(sqrt(a), sqrt(1-a))
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    // d = radius * c
    double d = EARTH_RADIUS * c;
    return d;
  }

  /*
   * // faster square distance approximator using equirectangular projection
   * private double equirectangularSq(RadianLatLng l) { // phi is lat, lambda is
   * lng double p1 = getLat(); // phi 1 double p2 = l.getLat(); // phi 2 double
   * dP = p2 - p1; // delta of phi double dL = l.getLng() - getLng(); // delta
   * of lambda // x = dL * cos(avg(p1, p2)) double x = dL * Math.cos((p1 +
   * p2)/2); // y = dP double y = dP; // d = R * sqrt(x^2 + y^2), d^2 = R^2 *
   * (x^2 + y^2) double d2 = EARTH_RADIUS * EARTH_RADIUS * (x*x + y*y); return
   * d2; } //
   */

  @Override
  public int getDimensions() {
    return LATLNG_DIM;
  }

  @Override
  public String toString() {
    return "(" + lat + ", " + lng + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof RadianLatLng)) {
      return false;
    }

    RadianLatLng r = (RadianLatLng) obj;

    return this.lat == r.lat && this.lng == r.lng;
  }

  @Override
  public int hashCode() {
    return (LATLNG_DIM * Double.hashCode(lat))
        ^ (EARTH_RADIUS * Double.hashCode(lng));
  }
}
