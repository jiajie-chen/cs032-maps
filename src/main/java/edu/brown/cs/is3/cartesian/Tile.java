package edu.brown.cs.is3.cartesian;

import java.util.Set;

import edu.brown.cs.is3.maps.CompactWay;

/**
 * Class representing a tile for sending as JSON. Extend south and west from
 * northwest corner based on size. Contain a set of compact ways for easy
 * storage.
 * @author is3
 *
 */
public class Tile {
  private final RadianLatLng nw;
  private final double size;
  private final Set<CompactWay> ways;

  public Tile(RadianLatLng nw, double size, Set<CompactWay> ways) {
    this.nw = nw;
    this.size = size;
    this.ways = ways;
  }

  @Override
  public String toString() {
    return "Corner: " + nw + " Size: " + size + " Ways: " + ways;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Tile)) {
      return false;
    }

    Tile t = (Tile) obj;

    return this.nw.equals(t.nw) && this.size == t.size
        && this.ways.equals(t.ways);
  }

  @Override
  public int hashCode() {
    return nw.hashCode() ^ Double.hashCode(size);
  }
}
