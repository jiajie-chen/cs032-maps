package edu.brown.cs.is3.cartesian;

import java.util.Comparator;

import edu.brown.cs.is3.maps.Node;

public class DistanceToComparator implements Comparator<Node> {
  private final Node end;

  public DistanceToComparator(Node end) {
    this.end = end;
  }

  @Override
  public int compare(Node one, Node two) {
    return -Double.compare(one.getDistance(end), two.getDistance(end));
  }

}
