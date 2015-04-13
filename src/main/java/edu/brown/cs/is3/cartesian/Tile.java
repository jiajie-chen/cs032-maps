package edu.brown.cs.is3.cartesian;

import java.util.Set;

import edu.brown.cs.is3.maps.CompactWay;

public class Tile {
  private final RadianLatLng nw;
  private final double size;
  private final Set<CompactWay> ways;

  public Tile(RadianLatLng nw, double size, Set<CompactWay> ways) {
    this.nw = nw;
    this.size = size;
    this.ways = ways;
  }
}
