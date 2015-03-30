package edu.brown.cs.jc124.data;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import edu.brown.cs.jc124.data.Graph.EdgeAttribute;

/**
 * JUnit Tests for DijkstraPathFinder.java class.
 */
public class DijkstraPathFinderTest {

  private static final int MAX_NODES = 20;

  // makes a random graph, nodes randomly connected or connected as a ring
  private Graph<Integer, String, String> newRandomGraph(int totNodes,
      boolean ring) {
    UndirectedGraph<Integer, String, String> g = new UndirectedGraph<>();

    for (int i = 0; i <= totNodes; i++) {
      g.addNode(i);
    }
    if (ring) {
      for (int i = 1; i <= totNodes; i++) {
        g.addEdge(i - 1, i);
      }
      g.addEdge(0, totNodes);
    } else {
      // add random edges
      for (int i = 0; i <= totNodes; i++) {
        for (int j = 0; j <= totNodes; j++) {
          if (Math.random() > 0.5) {
            g.addEdge(i, j);
          }
        }
      }
    }

    return g;
  }

  /**
   * Tests when there's a path from a start node to end node, where start and
   * end are connected by an edge
   */
  @Test
  public void directPath() {
    int maxN = (int) (Math.random() * MAX_NODES);
    Graph<Integer, String, String> g = newRandomGraph(maxN, false);
    maxN += 1;
    g.addNode(maxN);
    g.addEdge(0, maxN);

    PathFinder<Integer, String, String> p = new DijkstraPathFinder<>();
    Iterable<Integer> path = p.findPath(g, 0, maxN);
    Iterator<Integer> i = path.iterator();

    assertTrue(i.next().equals(0));
    assertTrue(i.next().equals(maxN));
    assertTrue(!i.hasNext());
  }

  /**
   * Test for when there are two paths to end from the start, and they're not
   * directly connected
   */
  @Test
  public void hasPath() {
    int maxN = (int) (Math.random() * MAX_NODES);
    // make ring graph
    Graph<Integer, String, String> g = newRandomGraph(maxN, true);

    // make maxN->0 connection very high
    g.addEdge(maxN, 0, new EdgeAttribute<String>() {
      @Override
      public String getAttribute() {
        return null;
      }

      @Override
      public double getEdgeWeight() {
        return MAX_NODES * MAX_NODES;
      }
    });

    PathFinder<Integer, String, String> p = new DijkstraPathFinder<>();
    Iterator<Integer> path = p.findPath(g, 0, maxN).iterator();

    // just need to check that a path exists
    assertTrue(path != null);

    // also check path is correct (from 0->1 .. ->maxN)
    for (int i = 0; i <= maxN; i++) {
      assertTrue(path.next().equals(i));
    }
  }

  /**
   * Test for when trying to path to an isolated node
   */
  @Test
  public void noPath() {
    int maxN = (int) (Math.random() * MAX_NODES);
    Graph<Integer, String, String> g = newRandomGraph(maxN, false);
    maxN += 1;
    g.addNode(maxN);

    PathFinder<Integer, String, String> p = new DijkstraPathFinder<>();
    Iterable<Integer> path = p.findPath(g, 0, maxN);

    assertTrue(path == null);
  }

}
