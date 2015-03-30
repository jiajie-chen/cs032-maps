package edu.brown.cs.jc124.bacon.graph;

import java.util.HashSet;
import java.util.Set;

import edu.brown.cs.jc124.data.Graph;

/**
 * @author jchen
 *
 *         Graph implementation for usage in Bacon.
 */
public class BaconGraph implements Graph<String, Actor, Movie> {
  private BaconRepository src;
  private Graph<String, Actor, Movie> intern;
  private Set<String> internedAdj;
  private boolean gotAllNode;

  /**
   * Makes a new graph using intern for a cache and src as the repository to
   * fetch info.
   *
   * @param src
   *          the repository to use
   * @param intern
   *          the caching graph to use
   */
  public BaconGraph(BaconRepository src, Graph<String, Actor, Movie> intern) {
    this.src = src;
    this.intern = intern;
    internedAdj = new HashSet<>();
    gotAllNode = false;
  }

  @Override
  public Set<String> getAdjacentNodes(String actorId) {
    // first see if the node is already interned
    if (internedAdj.contains(actorId)) {
      return intern.getAdjacentNodes(actorId);
    } else if (src.hasNode(actorId)) {
      // else, get from source and intern nodes and edges in graph
      Set<String> adj = src.getAdjacentNodes(actorId);

      intern.addNode(actorId, null);
      for (String n : adj) {
        // intern node prototype
        intern.addNode(n, null);
        // add edge prototype
        intern.addEdge(actorId, n, null);
      }

      internedAdj.add(actorId);
      return adj;
    }

    // node doesn't exist
    return null;
  }

  @Override
  public NodeAttribute<Actor> getNodeAttribute(String actorId) {
    // first see if the node is already interned
    NodeAttribute<Actor> a = intern.getNodeAttribute(actorId);
    if (a == null && src.hasNode(actorId)) {
      // else, get from source and intern node and attribute in graph
      a = src.getNodeAttribute(actorId);
      // intern by adding all adjacent nodes (thus adding the node, and all
      // adjacent prototypes)
      // getAdjacentNodes(actorId);
    }

    return a;
  }

  @Override
  public EdgeAttribute<Movie> getEdgeAttribute(String actorBegin,
      String actorEnd) {
    // first see if the edge is already interned
    EdgeAttribute<Movie> m = intern.getEdgeAttribute(actorBegin, actorEnd);
    if (m == null && src.hasEdge(actorBegin, actorEnd)) {
      // else, get from source and intern nodes, edges and attribute in graph
      m = src.getEdgeAttribute(actorBegin, actorEnd);

      // add node prototypes
      intern.addNode(actorBegin, null);
      intern.addNode(actorEnd, null);
      // add actual edge
      intern.addEdge(actorBegin, actorEnd, m);
    }

    return m;
  }

  @Override
  public Set<String> getAllNodeKeys() {
    if (gotAllNode) {
      return intern.getAllNodeKeys();
    }

    // get all, and intern all
    Set<String> all = src.getAllNodeKeys();
    for (String id : all) {
      intern.addNode(id, null);
    }
    return all;
  }

  @Override
  public boolean hasNode(String actorId) {
    boolean hasNode = intern.hasNode(actorId);
    if (!hasNode) {
      hasNode = src.hasNode(actorId);

      // if src has edge, intern
      if (hasNode) {
        intern.addNode(actorId, null);
      }
    }
    return hasNode;
  }

  @Override
  public boolean hasEdge(String actorBegin, String actorEnd) {
    boolean hasEdge = intern.hasEdge(actorBegin, actorEnd);
    if (!hasEdge) {
      hasEdge = src.hasEdge(actorBegin, actorEnd);

      // if src has edge, intern
      if (hasEdge) {
        intern.addNode(actorBegin, null);
        intern.addNode(actorEnd, null);
        intern.addEdge(actorBegin, actorEnd, null);
      }
    }
    return hasEdge;
  }

  @Override
  public boolean clear() {
    internedAdj.clear();
    gotAllNode = false;
    return intern.clear();
  }

  // ******************* UNUSED METHODS *******************
  @Override
  public final boolean addNode(String actorId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean addNode(String actorId, NodeAttribute<Actor> a) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean addEdge(String actorBegin, String actorEnd) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean addEdge(String actorBegin, String actorEnd,
      EdgeAttribute<Movie> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean removeNode(String actorId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean removeNodeAttribute(String actorId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean removeEdge(String actorBegin, String actorEnd) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean removeEdgeAttribute(String actorBegin, String actorEnd) {
    throw new UnsupportedOperationException();
  }
}
