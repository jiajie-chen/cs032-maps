package edu.brown.cs.is3.maps;

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

  @Override
  public int compareTo(WeightedNode that) {
    return Double.compare(this.weight, that.weight);
  }

}
