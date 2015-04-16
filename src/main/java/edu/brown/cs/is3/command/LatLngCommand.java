package edu.brown.cs.is3.command;

import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;

public class LatLngCommand implements Command {
  private final double lat1, lng1, lat2, lng2;

  public LatLngCommand(String[] args) {
    this.lat1 = Double.parseDouble(args[0]);
    this.lng1 = Double.parseDouble(args[1]);
    this.lat2 = Double.parseDouble(args[2]);
    this.lng2 = Double.parseDouble(args[3]);

    if (lat1 > 90 || lat1 < -90 || lng1 > 180 || lng1 < -180
        || lat2 > 90 || lat2 < -90 || lng2 > 180 || lng2 < -180) {
      throw new IllegalArgumentException(
          "Latitude and longitude must range between -90 and 90; and -180 and 180, respectively.");
    }
  }

  @Override
  public Path run(MapsManager m) {
    return m.getPathByPoints(lat1, lng1, lat2, lng2);
  }
}
