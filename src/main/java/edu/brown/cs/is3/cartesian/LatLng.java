package edu.brown.cs.is3.cartesian;

public class LatLng {
  private final double lat;
  private final double lng;

  public LatLng(Double lat, Double lng) {
    if (lat >= 90 || lat <= -90 || lng >= 180 || lng <= -180) {
      throw new IllegalArgumentException("Latitude and longitude must "
          + "remain between plus or minus 180 and 360 respectively.");
    }

    this.lat = lat;
    this.lng = lng;
  }

  public double getDistance(LatLng that) {
    double theta = this.lng - that.lng;
    double dist = Math.sin(deg2rad(this.lat)) * Math.sin(deg2rad(that.lat))
        + Math.cos(deg2rad(this.lat)) * Math.cos(deg2rad(that.lat))
        * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;

    return (dist);
  }

  private double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  private double rad2deg(double rad) {
    return (rad * 180 / Math.PI);
  }
}
