package edu.brown.cs.is3.command;

import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;

public class LatLngCommand implements Command {
  private final String[] args;

  public LatLngCommand(String[] args) {
    this.args = args;
  }

  @Override
  public Path run(MapsManager m) {
    double lat1 = Double.parseDouble(args[0]);
    double lng1 = Double.parseDouble(args[1]);
    double lat2 = Double.parseDouble(args[2]);
    double lng2 = Double.parseDouble(args[3]);

    return m.getPathByPoints(lat1, lng1, lat2, lng2);
  }

}
