package edu.brown.cs.is3.cartesian;

import java.util.Comparator;
import java.util.Map;

import edu.brown.cs.is3.maps.Node;

/**
 * Comparator class for sorting by a-star distance.
 * @author is3
 *
 */
public class DistanceToComparator implements Comparator<Node> {
  private final Node target;
  private final Map<Node, Double> distances;

  /**
   * Builds a distance estimator for calculating f using a target node (to find
   * h) and a current distances map (to find g).
   * @param target node being searched for.
   * @param distances map of minimum distances to reach nodes and nodes.
   */
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
