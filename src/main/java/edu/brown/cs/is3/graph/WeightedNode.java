package edu.brown.cs.is3.graph;

import edu.brown.cs.is3.maps.Node;

/**
 * Compact way of storing nodes for use in a priority queue.
 * @author is3
 *
 */
public class WeightedNode implements Comparable<WeightedNode> {
  private final Node ele;
  private final double weight;

  /**
   * Wraps a node with a weight.
   * @param ele node to wrap.
   * @param weight or distance estimate.
   */
  public WeightedNode(Node ele, double weight) {
    this.ele = ele;
    this.weight = weight;
  }

  /**
   * @return the ele
   */
  public Node getEle() {
    return ele;
  }

  /**
   * @return the weight
   */
  public double getWeight() {
    return weight;
  }

  /**
   * NOTE: Comparator does not match equals.
   */
  @Override
  public int compareTo(WeightedNode that) {
    return Double.compare(this.weight, that.weight);
  }

  // @Override
  // public boolean equals(Object obj) {
  // if (this == obj) {
  // return true;
  // }
  //
  // if (!(obj instanceof WeightedNode)) {
  // return false;
  // }
  //
  // WeightedNode w = (WeightedNode) obj;
  //
  // return this.ele.equals(w.ele);
  // }
  //
  // @Override
  // public int hashCode() {
  // return ele.hashCode();
  // }
}
