package edu.brown.cs.is3.maps;

import edu.brown.cs.is3.cartesian.RadianLatLng;

public class CompactWay {
  private final RadianLatLng start;
  private final RadianLatLng end;

  public CompactWay(RadianLatLng start, RadianLatLng end) {
    this.start = start;
    this.end = end;
  }

  /**
   * @return the end position of this way.
   */
  public RadianLatLng getStart() {
    return this.start;
  }

  /**
   * @return the start position of this way.
   */
  public RadianLatLng getEnd() {
    return this.end;
  }
}
