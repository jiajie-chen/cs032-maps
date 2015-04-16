package edu.brown.cs.is3.command;

import edu.brown.cs.is3.cartesian.RadianLatLng;
import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;

/**
 * Command class for executing point to point searches.
 * @author is3
 *
 */
public class LatLngCommand implements Command {
  private final double lat1, lng1, lat2, lng2;

  private static final int FIRST_LAT = 0;
  private static final int FIRST_LNG = 1;
  private static final int SECOND_LAT = 2;
  private static final int SECOND_LNG = 3;

  /**
   * Builds a command out of an array of REPL args. These args must be parseable
   * doubles.
   * @param args array of double parseable strings.
   */
  public LatLngCommand(String[] args) {
    this.lat1 = Double.parseDouble(args[FIRST_LAT]);
    this.lng1 = Double.parseDouble(args[FIRST_LNG]);
    this.lat2 = Double.parseDouble(args[SECOND_LAT]);
    this.lng2 = Double.parseDouble(args[SECOND_LNG]);

    if (lat1 > RadianLatLng.MAX_LAT || lat1 < -RadianLatLng.MAX_LAT
        || lng1 > RadianLatLng.MAX_LNG || lng1 < -RadianLatLng.MAX_LNG
        || lat2 > RadianLatLng.MAX_LAT || lat2 < -RadianLatLng.MAX_LAT
        || lng2 > RadianLatLng.MAX_LNG || lng2 < -RadianLatLng.MAX_LNG) {
      throw new IllegalArgumentException(
          "Latitude and longitude must range between -90 and "
              + "90 and -180 and "
              + "180, respectively.");
    }
  }

  @Override
  public Path run(MapsManager m) {
    return m.getPathByPoints(lat1, lng1, lat2, lng2);
  }
}
