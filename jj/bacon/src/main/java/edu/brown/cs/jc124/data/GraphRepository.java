package edu.brown.cs.jc124.data;

import java.util.Set;

import edu.brown.cs.jc124.data.Graph.EdgeAttribute;
import edu.brown.cs.jc124.data.Graph.NodeAttribute;

/**
 * @author jchen
 *
 *         Interface for implementation of a data source for a graph.
 *
 * @param <K>
 *          key to get nodes by
 * @param <N>
 *          attribute of nodes
 * @param <J>
 *          key to get edges by
 * @param <E>
 *          attribute of edges
 */
public interface GraphRepository<K, N, J, E> {
  /**
   * Gets all nodes adjacent to the node.
   *
   * @param nodeKey
   *          the node to get adjacents from
   * @return the node keys adjacent to the node
   */
  Set<K> getAdjacentNodes(K nodeKey);

  /**
   * Gets a node's attribute from the graph.
   *
   * @param nodeKey
   *          the node to get
   * @return the attribute of the node, or null if doesn't exist
   */
  NodeAttribute<N> getNodeAttribute(K nodeKey);

  /**
   * Gets an edge's attribute from the graph.
   *
   * @param begin
   *          the beginning node
   * @param end
   *          the ending node
   * @return the attribute of the edge, or null if it doesn't exist
   */
  EdgeAttribute<E> getEdgeAttribute(K begin, K end);

  /**
   * Gets an edge's attribute from the graph.
   *
   * @param edgeKey
   *          the edge key to get
   * @return the attribute of the edge, or null if it doesn't exist
   */
  EdgeAttribute<E> getEdgeAttribute(J edgeKey);

  /**
   * Gets all node keys in the graph.
   *
   * @return all nodes in graph
   */
  Set<K> getAllNodeKeys();
  /**
   * Gets all edge keys in the graph.
   *
   * @return all edges in graph
   */
  Set<J> getAllEdgeKeys();

  /**
   * Determines if graph has a node.
   *
   * @param nodeKey
   *          the node key to get
   * @return true if graph has node
   */
  boolean hasNode(K nodeKey);

  /**
   * Determines if graph has an edge.
   *
   * @param begin
   *          the beginning node
   * @param end
   *          the ending node
   * @return true if graph has edge
   */
  boolean hasEdge(K begin, K end);

  /**
   * Determines if graph has an edge.
   *
   * @param edgeKey
   *          the edge key to get
   * @return true if graph has edge
   */
  boolean hasEdge(J edgeKey);

  /**
   * Clears out all data held in the repo.
   *
   * @return true if graph was changed
   */
  boolean clear();
}
