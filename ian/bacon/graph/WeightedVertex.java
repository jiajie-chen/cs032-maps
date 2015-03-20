package edu.brown.cs.is3.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Vertex of graphing holding data and nonneagative weights.
 * @author is3
 *
 * @param <E> type of ele contained.
 */
public class WeightedVertex<E> implements Comparable<WeightedVertex<E>> {
  private double weight;
  private final E ele;
  private boolean explored = false;
  private WeightedVertex<E> prev = null;
  private Edge<E> prevEdge = null;
  private Set<Edge<E>> edges = new HashSet<>();

  /**
   * Builds a weighted vertex from an element and a weight.
   * @param ele contained by vertex.
   * @param weight distance ele is from source.
   */
  public WeightedVertex(E ele, double weight) {
    if (weight < 0) {
      throw new IllegalArgumentException("ERROR: Weights must be nonnegative.");
    }

    this.weight = weight;
    this.ele = ele;
  }

  /**
   * @return the edges
   */
  public Set<Edge<E>> getEdges() {
    return this.edges;
  }

  /**
   * @return the weight.
   */
  public double getWeight() {
    return this.weight;
  }

  /**
   * @param weight to set.
   */
  public void setWeight(double weight) {
    this.weight = weight;
  }

  /**
   * @param prev node to set.
   */
  public void setPrev(WeightedVertex<E> prev) {
    this.prev = prev;
  }

  /**
   * @return the previous node.
   */
  public WeightedVertex<E> getPrev() {
    return prev;
  }

  @Override
  public int hashCode() {
    return ele.hashCode();
  }

  @Override
  /**
   * WARNING: Comparator is not consistent with equals.
   */
  public int compareTo(WeightedVertex<E> that) {
    return Double.compare(this.weight, that.weight);
  }

  /**
   * @return the vertex held by this object
   */
  public E getElement() {
    return this.ele;
  }

  /**
   * @return true if node has been explored and false otherwise.
   */
  public boolean isExplored() {
    return this.explored;
  }

  /**
   * Sets explored to true.
   */
  public void setExplored() {
    this.explored = true;
  }

  /**
   * Sets the edge that leads to this vertex.
   * @param e edge.
   */
  public void setPrevEdge(Edge<E> e) {
    this.prevEdge = e;
  }

  /**
   * @return edge that leads to this one.
   */
  public Edge<E> getPrevEdge() {
    return this.prevEdge;
  }

  /**
   * @param edge to add to vertex.
   */
  public void addEdge(Edge<E> edge) {
    edges.add(edge);
  }

  @Override
  public String toString() {
    return "[E: " + ele + " Explored: " + explored + " Weight: " + weight
        + " Edges: " + edges.size() + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof WeightedVertex)) {
      return false;
    }

    WeightedVertex<?> v = (WeightedVertex<?>) o;

    return this.ele.equals(v.ele);
  }

}
