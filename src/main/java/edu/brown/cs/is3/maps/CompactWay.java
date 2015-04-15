package edu.brown.cs.is3.maps;

import edu.brown.cs.is3.cartesian.RadianLatLng;

/**
 * Compact way of representing ways for transmission by JSON. Directionality of
 * the way is not guaranteed. Don't take directionality into account for hashing
 * or equals.
 * @author is3
 *
 */
public class CompactWay {
  private final String id;
  private final RadianLatLng start;
  private final RadianLatLng end;

  public CompactWay(RadianLatLng start, RadianLatLng end, String id) {
    this.start = start;
    this.end = end;
    this.id = id;
  }
  
  public CompactWay(RadianLatLng start, RadianLatLng end) {
    this.start = start;
    this.end = end;
    this.id = "";
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

  @Override
  public String toString() {
    return "(Start: " + start + ", End: " + end + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof CompactWay)) {
      return false;
    }

    CompactWay w = (CompactWay) obj;

    return (this.start.equals(w.start) && this.end.equals(w.end))
        || (this.end.equals(w.start) && this.start.equals(w.end));
  }

  @Override
  public int hashCode() {
    return (Double.hashCode(start.getLat()) ^ Double.hashCode(end.getLat()))
        ^ (Double.hashCode(start.getLng()) ^ Double.hashCode(end.getLng()));
  }
}
