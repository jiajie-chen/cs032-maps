package edu.brown.cs.jc124.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author jchen
 *
 *         Basic implementation of an undirected graph.
 *
 * @param <K>
 *          key to hold nodes by
 * @param <N>
 *          node attribute
 * @param <E>
 *          edge attribute
 */
public class UndirectedGraph<K, N, E> implements Graph<K, N, E> {
  private Map<K, NodeAttribute<N>> nodes;
  // use adjacency list with attributes (first k is source, second is
  // destination)
  private Map<K, Map<K, EdgeAttribute<E>>> edges;

  /**
   * Makes an empty graph.
   */
  public UndirectedGraph() {
    nodes = new HashMap<>();
    edges = new HashMap<>();
  }

  @Override
  public boolean addNode(K nodeKey) {
    return addNode(nodeKey, null);
  }

  @Override
  public boolean addNode(K nodeKey, NodeAttribute<N> nodeAttr) {
    // don't override existing attribute with null
    if (nodeAttr == null && nodes.containsKey(nodeKey)) {
      return false;
    }

    nodes.put(nodeKey, nodeAttr);

    return true;
  }

  @Override
  public boolean addEdge(K begin, K end) {
    return addEdge(begin, end, null);
  }

  @Override
  public boolean addEdge(K begin, K end, EdgeAttribute<E> edgeAttr) {
    if (!nodes.containsKey(begin) || !nodes.containsKey(end)) {
      return false;
    }

    if (edgeAttr == null && hasEdge(begin, end)) {
      return false;
    }

    // add begin->end
    edges.putIfAbsent(begin, new HashMap<>());
    edges.get(begin).put(end, edgeAttr);
    // add end->begin
    edges.putIfAbsent(end, new HashMap<>());
    edges.get(end).put(begin, edgeAttr);
    return true;
  }

  @Override
  public boolean removeNode(K nodeKey) {

    if (!hasNode(nodeKey)) {
      return false;
    }

    // unlink edges other->nodeKey
    Set<K> linked = edges.keySet();
    for (K other : linked) {
      edges.get(other).remove(nodeKey);
    }
    // now remove link nodeKey->other
    edges.remove(nodeKey);

    // finally, remove node from mapping
    nodes.remove(nodeKey);

    return true;
  }

  @Override
  public boolean removeNodeAttribute(K nodeKey) {
    if (getNodeAttribute(nodeKey) == null) {
      return false;
    }

    edges.put(nodeKey, null);

    return true;
  }

  @Override
  public boolean removeEdge(K begin, K end) {
    if (!hasEdge(begin, end)) {
      return false;
    }

    // unlink edges begin->end
    edges.get(begin).remove(end);
    // now remove link end->begin
    edges.get(end).remove(begin);

    return true;
  }

  @Override
  public boolean removeEdgeAttribute(K begin, K end) {
    if (getEdgeAttribute(begin, end) == null) {
      return false;
    }

    // unlink edges begin->end
    edges.get(begin).put(end, null);
    // now remove link end->begin
    edges.get(end).put(begin, null);

    return true;
  }

  @Override
  public NodeAttribute<N> getNodeAttribute(K nodeKey) {
    if (!hasNode(nodeKey)) {
      return null;
    }
    return nodes.get(nodeKey);
  }

  @Override
  public EdgeAttribute<E> getEdgeAttribute(K begin, K end) {
    if (!hasEdge(begin, end)) {
      return null;
    }
    return edges.getOrDefault(begin, new HashMap<>()).get(end);
  }

  @Override
  public Set<K> getAdjacentNodes(K nodeKey) {
    Map<K, EdgeAttribute<E>> ends = edges.get(nodeKey);

    if (ends == null) {
      // isolated key
      if (nodes.containsKey(nodeKey)) {
        return new HashSet<>();
      }
      return null;
    }

    return ends.keySet();
  }

  @Override
  public Set<K> getAllNodeKeys() {
    return nodes.keySet();
  }

  @Override
  public boolean hasNode(K nodeKey) {
    return nodes.containsKey(nodeKey);
  }

  @Override
  public boolean hasEdge(K begin, K end) {
    return edges.getOrDefault(begin, new HashMap<>()).containsKey(end);
  }

  @Override
  public boolean clear() {
    if (nodes.isEmpty() && edges.isEmpty()) {
      return false;
    }

    nodes.clear();
    edges.clear();

    return true;
  }

}
