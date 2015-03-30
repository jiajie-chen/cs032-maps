package edu.brown.cs.jc124.data;

import java.util.Set;

/**
 * @author jchen
 *
 *         Interface for pathfinding algorithms over a graph.
 *
 * @param <K>
 *          graph's node key
 * @param <N>
 *          graph's node attribute
 * @param <E>
 *          graph's edge attribute
 */
public interface PathFinder<K, N, E> {
  /**
   * Gets the neighboring nodes in a graph, from a node.
   *
   * @param g
   *          the graph to pull from
   * @param current
   *          the node to get neighbors of
   * @return null if node doesn't exist, else the neighboring nodes
   */
  Set<K> nextNodes(Graph<K, N, E> g, K current);

  /**
   * Finds a path in the graph from node to node.
   *
   * @param g
   *          the graph to pull from
   * @param begin
   *          the starting node
   * @param end
   *          the ending node
   * @return the path, as an ordering of node keys, from begin to end; null if
   *         no path
   */
  Iterable<K> findPath(Graph<K, N, E> g, K begin, K end);
}
