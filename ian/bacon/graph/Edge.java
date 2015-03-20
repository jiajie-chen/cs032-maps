package edu.brown.cs.is3.graph;

/**
 * Class connecting two vertices.
 * @author is3
 *
 * @param <E> generic type of vertices.
 */
public class Edge<E> {
  private final WeightedVertex<E> source;
  private final WeightedVertex<E> target;
  private final double weight;

  /**
   * Constructs an edge.
   * @param sourceVertex vertex starting edge.
   * @param targetVertex vertex ending edge.
   * @param weight of the edge.
   */
  public Edge(WeightedVertex<E> sourceVertex, WeightedVertex<E> targetVertex,
      double weight) {

    this.source = sourceVertex;
    this.target = targetVertex;
    this.weight = weight;
  }

  /**
   * @return the source
   */
  public WeightedVertex<E> getSource() {
    return source;
  }

  /**
   * @return the target
   */
  public WeightedVertex<E> getTarget() {
    return target;
  }

  /**
   * @return the weight
   */
  public double getWeight() {
    return weight;
  }

  @Override
  public String toString() {
    return "Source: " + source + " Target: " + target + " Weight: " + weight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Edge)) {
      return false;
    }

    Edge<?> e = (Edge<?>) o;

    return this.source == e.source && this.target.equals(e.target)
        && this.weight == e.weight;
  }

  @Override
  public int hashCode() {
    return (int) ((source.hashCode() ^ target.hashCode()) * weight);
  }
}
