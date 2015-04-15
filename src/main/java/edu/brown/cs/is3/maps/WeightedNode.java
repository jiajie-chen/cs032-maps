package edu.brown.cs.is3.maps;

public class WeightedNode implements Comparable<WeightedNode> {
  private final Node ele;
  private final double weight;

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
