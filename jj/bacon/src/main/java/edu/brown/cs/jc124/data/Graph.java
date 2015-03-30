package edu.brown.cs.jc124.data;

import java.util.Set;

/**
 * @author jchen
 *
 *         Interface for implementing graphs.
 *
 * @param <K>
 *          the key to get nodes by
 * @param <N>
 *          the type to store at nodes
 * @param <E>
 *          the type to store at edges
 */
public interface Graph<K, N, E> {
  /**
   * @author jchen
   *
   *         Wraps a given type for storage in a node.
   *
   * @param <N>
   *          the type to store
   */
  interface NodeAttribute<N> extends Attribute<N> {
  }

  /**
   * @author jchen
   *
   *         Wraps a given type for storage in an edge.
   *
   * @param <E>
   *          the type to store
   */
  interface EdgeAttribute<E> extends Attribute<E> {
    double getEdgeWeight();
  }

  /**
   * Adds a node to the graph, if not in the graph.
   *
   * @param nodeKey
   *          the key to add
   * @return true if node was added and not in graph
   */
  boolean addNode(K nodeKey);

  /**
   * Adds a node with attribute to the graph, if not in the graph. Updates
   * attribute if not null.
   *
   * @param nodeKey
   *          the key to add
   * @param nodeAttr
   *          the attribute to bind to the key
   * @return true if node was added and node or attribute not in graph
   */
  boolean addNode(K nodeKey, NodeAttribute<N> nodeAttr);

  /**
   * Adds an edge to the graph from node to node.
   *
   * @param begin
   *          the beginning node
   * @param end
   *          the ending node
   * @return true if the edge wasn't in the graph and was added
   */
  boolean addEdge(K begin, K end);

  /**
   * Adds a edge with attribute to the graph, if not in the graph. Updates
   * attribute if not null.
   *
   * @param begin
   *          the beginning node
   * @param end
   *          the ending node
   * @param edgeAttr
   *          the attribute to bind to the edge
   * @return true if edge was added and edge or attribute not in graph
   */
  boolean addEdge(K begin, K end, EdgeAttribute<E> edgeAttr);

  /**
   * Removes node from the graph.
   *
   * @param nodeKey
   *          the key to remove
   * @return true if graph was changed
   */
  boolean removeNode(K nodeKey);

  /**
   * Removes the attribute from a node.
   *
   * @param nodeKey
   *          the node whose attribute to remove
   * @return true if graph was changed
   */
  boolean removeNodeAttribute(K nodeKey);

  /**
   * Removes an edge from the graph.
   *
   * @param begin
   *          the beginning node
   * @param end
   *          the ending node
   * @return true if graph was changed
   */
  boolean removeEdge(K begin, K end);

  /**
   * Removes an edge's attribute from the graph.
   *
   * @param begin
   *          the beginning node
   * @param end
   *          the ending node
   * @return true if graph was changed
   */
  boolean removeEdgeAttribute(K begin, K end);

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
   * Gets all nodes adjacent to the node.
   *
   * @param nodeKey
   *          the node to get adjacents from
   * @return the node keys adjacent to the node
   */
  Set<K> getAdjacentNodes(K nodeKey);

  /**
   * Gets all nodes in the graph.
   *
   * @return all nodes in graph
   */
  Set<K> getAllNodeKeys();

  /**
   * Determines if graph has a node.
   *
   * @param nodeKey
   *          node to check
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
   * Clears out all data in graph.
   *
   * @return true if graph was changed
   */
  boolean clear();
}
