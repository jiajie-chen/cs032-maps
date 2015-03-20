package edu.brown.cs.is3.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Representation of generic graph with nonnegative weights.
 * @author is3
 *
 * @param <E> type of generic graph.
 */
public class Graph<E> {

  /**
   * Shorter method signature that performs djikstras algorithm with no
   * additional checks.
   * @param startVertex vertex to start at.
   * @param endVertex vertex to end at.
   * @return a list of the edges to follow get from start to end.
   */
  public List<Edge<E>> dijkstras(WeightedVertex<E> startVertex,
      WeightedVertex<E> endVertex) {

    return dijkstras(startVertex, endVertex, null);
  }

  /**
   * Longer method signature including an additional checker. Same as above.
   * @param start vertex.
   * @param end vertex.
   * @param check a checker to check edge validity.
   * @return list of the edges to follow to get from start to end.
   */
  public List<Edge<E>> dijkstras(WeightedVertex<E> start,
      WeightedVertex<E> end, ConnectionChecker<E> check) {

    if (check == null) {
      check = new DummyChecker<>();
    }

    if (start == null || end == null) {
      return new ArrayList<>();
    }

    if (start.equals(end)) {
      List<Edge<E>> toReturn = new ArrayList<>();
      toReturn.add(new Edge<E>(start, end, 0));
      return toReturn;
    }

    System.out.println("Started DJK!");

    PriorityQueue<WeightedVertex<E>> pq = new PriorityQueue<>();
    HashMap<E, Double> distances = new HashMap<>();
    WeightedVertex<E> curr = start;
    pq.add(curr);
    distances.put(start.getElement(), 0.0);

    while (!pq.isEmpty()) {
      curr = pq.poll();
      System.out.println("Curr: " + curr);

      if (curr.equals(end)) {
        return generateSolution(curr);
      }

      curr.setExplored();
      Set<Edge<E>> currEdges = curr.getEdges();
      for (Edge<E> e : currEdges) {
        if (!check.validConnection(e)) {
          continue;
        }

        WeightedVertex<E> v = e.getTarget();

        if (!v.isExplored()) {
          if (!pq.contains(v)) {
            v.setWeight(curr.getWeight() + e.getWeight());
            pq.add(v);
            distances.put(v.getElement(), v.getWeight());
            v.setPrev(curr);
            v.setPrevEdge(e);
          } else {
            Double cost = distances.get(v.getElement());
            if (cost != null && cost > curr.getWeight() + e.getWeight()) {
              pq.remove(v);
              v.setWeight(curr.getWeight() + e.getWeight());
              pq.add(v);
              distances.put(v.getElement(), v.getWeight());
              v.setPrev(curr);
              v.setPrevEdge(e);
            }
          }
        }
      }
    }

    return new ArrayList<>();
  }

  /**
   * Traces the previous nodes back to build a path from start to end.
   * @param curr the node the search ended on.
   * @return a list of the edges needed to get form start to curr.
   */
  private List<Edge<E>> generateSolution(WeightedVertex<E> curr) {
    List<Edge<E>> toReturn = new ArrayList<>();
    if (curr == null) {
      return toReturn;
    }

    while (curr.getPrev() != null) {
      toReturn.add(0, curr.getPrevEdge());
      curr = curr.getPrev();
    }

    return toReturn;
  }
}
