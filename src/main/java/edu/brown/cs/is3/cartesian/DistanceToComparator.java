package edu.brown.cs.is3.cartesian;

import java.util.Comparator;
import java.util.Map;

import edu.brown.cs.is3.maps.Node;

public class DistanceToComparator implements Comparator<Node> {
  private final Node target;
  private final Map<Node, Double> distances;

  public DistanceToComparator(Node target, Map<Node, Double> distances) {
    this.target = target;
    this.distances = distances;
  }

  @Override
  public int compare(Node n, Node m) {
    double nTotal = distances.get(n) + n.getDistance(target);
    double mTotal = distances.get(m) + m.getDistance(target);

    return Double.compare(nTotal, mTotal);
  }

}
