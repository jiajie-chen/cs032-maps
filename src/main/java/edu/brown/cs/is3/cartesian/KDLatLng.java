//package edu.brown.cs.is3.cartesian;
//
//import com.javadocmd.simplelatlng.LatLng;
//
//import edu.brown.cs.jc124.kdtree.Coordinate;
//
///**
// * Implementation of SimpleLatLng that is compatible with KDTrees.
// * @author is3
// *
// */
//public class KDLatLng extends LatLng implements Coordinate {
//  /**
//   * Auto generated serial number.
//   */
//  private static final long serialVersionUID = 5912498506130680417L;
//
//  public static final int LATLNG_DIM = 2;
//
//  public static final int LAT_AXIS = 0;
//  public static final int LNG_AXIS = 1;
//
//  /**
//   * Builds a KDLatLng
//   * @param latitude of pos.
//   * @param longitude of pos.
//   */
//  public KDLatLng(double latitude, double longitude) {
//    super(latitude, longitude);
//  }
//
//  @Override
//  public int getDimensions() {
//    return LATLNG_DIM;
//  }
//
//  @Override
//  public double getField(int axis) {
//    switch (axis) {
//      case LAT_AXIS:
//        return this.getLatitude();
//      case LNG_AXIS:
//        return this.getLongitude();
//      default:
//        throw new IllegalArgumentException(
//            "Dimension " + axis
//+ " exceeds number of dimensions " + LATLNG_DIM);
//    }
//  }
//
//  @Override
//  public double squaredDistance(Coordinate c) {
//    return Math.pow(this.distance(c), 2);
//  }
//
//  @Override
//  public double distance(Coordinate c) {
//    // TODO Auto-generated method stub
//    return 0;
//  }
//
// }
