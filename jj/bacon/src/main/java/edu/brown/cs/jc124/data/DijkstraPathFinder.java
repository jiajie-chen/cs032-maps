package edu.brown.cs.jc124.data;

import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.brown.cs.jc124.data.Graph.EdgeAttribute;

/**
 * @author jchen
 *
 *         A shortest distance path finder that uses Dijkstra's algorithm.
 *
 * @param <K>
 *          Graph's key type
 * @param <N>
 *          Graph's node type
 * @param <E>
 *          Graph's edge type
 */
public class DijkstraPathFinder<K, N, E> implements PathFinder<K, N, E> {
  // make a comparator to rank by distance in a distance mapping
  private Comparator<K> getDistComparator(Map<K, Double> dist) {
    return new Comparator<K>() {
      @Override
      public int compare(K k1, K k2) {
        double d1 = dist.get(k1);
        double d2 = dist.get(k2);
        return (int) Math.signum(d1 - d2);
      }

    };
  }

  @Override
  public Iterable<K> findPath(Graph<K, N, E> g, K begin, K end) {
    if (!g.hasNode(begin) || !g.hasNode(end)) {
      return null;
    }

    // stores distance of each nodes
    Map<K, Double> dist = new HashMap<>();
    // stores the path mapping from end node mapped down to the start node
    Map<K, K> prev = new HashMap<>();
    // unvisited nodes
    List<K> frontier = new LinkedList<>();
    // visited nodes
    Set<K> explored = new HashSet<>();

    dist.put(begin, 0.0);
    prev.put(begin, null);
    frontier.add(begin);

    // distance comparator for sorting
    Comparator<K> comp = getDistComparator(dist);
    while (!frontier.isEmpty()) {
      // get minimal node
      frontier.sort(comp);
      K curr = frontier.remove(0);

      // check if end is found
      if (curr.equals(end)) {
        // generate shortest path by running through prev backwards
        Deque<K> shortPath = new LinkedList<>();
        while (curr != null) {
          shortPath.push(curr);
          curr = prev.get(curr);
        }
        return shortPath;
      }

      // else, look at the neighbors
      explored.add(curr);
      Set<K> nbrs = nextNodes(g, curr);
      for (K next : nbrs) {
        if (!explored.contains(next)) {
          // if not in frontier, add it
          if (!frontier.contains(next)) {
            frontier.add(next);
          }

          // calculate distance from this node to next node
          double w = 1; // default weight is 1
          EdgeAttribute<E> edge = g.getEdgeAttribute(curr, next);
          if (edge != null) {
            w = edge.getEdgeWeight();
          }
          double newDist = dist.get(curr) + w;

          // if better distance found
          if (newDist <= dist.getOrDefault(next, Double.POSITIVE_INFINITY)) {
            // update mappings
            dist.put(next, newDist);
            prev.put(next, curr);
          }
        }
      }
    }

    // all nodes exhausted, not found
    return null;
  }

  @Override
  public Set<K> nextNodes(Graph<K, N, E> g, K current) {
    return g.getAdjacentNodes(current);
  }
}
