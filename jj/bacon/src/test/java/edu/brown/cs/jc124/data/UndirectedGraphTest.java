package edu.brown.cs.jc124.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UndirectedGraphTest {

  private static final int MAX_NODES = 20;

  @Test
  public void addNode() {
    UndirectedGraph<Integer, String, String> g = new UndirectedGraph<>();

    // first node
    int node = (int) (Math.random() * (MAX_NODES / 2));
    assertTrue(!g.hasNode(node));

    assertTrue(g.addNode(node));
    assertTrue(g.hasNode(node));

    // second node
    node += (int) (Math.random() * (MAX_NODES / 2));
    assertTrue(!g.hasNode(node));

    assertTrue(g.addNode(node));
    assertTrue(g.hasNode(node));

    // re adding second node
    assertTrue(!g.addNode(node));
    assertTrue(g.hasNode(node));
  }

  @Test
  public void removeNode() {
    UndirectedGraph<Integer, String, String> g = new UndirectedGraph<>();

    // add nodes
    int node1 = (int) (Math.random() * (MAX_NODES / 2));
    assertTrue(!g.hasNode(node1));

    // also test removal when there is none
    assertTrue(!g.removeNode(node1));

    g.addNode(node1);

    int node2 = node1 + (int) (Math.random() * (MAX_NODES / 2));
    assertTrue(!g.hasNode(node2));

    g.addNode(node2);

    // add edge between them
    g.addEdge(node1, node2);

    // remove last node
    assertTrue(g.removeNode(node2));
    assertTrue(!g.hasNode(node2));
    assertTrue(g.hasNode(node1));

    // check that edges are also removed
    assertTrue(!g.hasEdge(node1, node2));

    // retry removal
    assertTrue(!g.removeNode(node2));
    assertTrue(!g.hasNode(node2));
    assertTrue(g.hasNode(node1));
  }

  @Test
  public void addEdges() {
    UndirectedGraph<Integer, String, String> g = new UndirectedGraph<>();

    // add nodes
    int node1 = (int) (Math.random() * (MAX_NODES / 2));
    g.addNode(node1);
    int node2 = node1 + (int) (Math.random() * (MAX_NODES / 2));
    g.addNode(node2);

    assertTrue(!g.hasEdge(node1, node2));
    assertTrue(!g.hasEdge(node2, node1));
    // add edge between them
    assertTrue(g.addEdge(node1, node2));
    assertTrue(g.hasEdge(node1, node2));
    assertTrue(g.hasEdge(node2, node1));

    // add again
    assertTrue(!g.addEdge(node1, node2));
    assertTrue(g.hasEdge(node1, node2));
    assertTrue(g.hasEdge(node2, node1));
  }

  @Test
  public void removeEdges() {
    UndirectedGraph<Integer, String, String> g = new UndirectedGraph<>();

    // add nodes
    int node1 = (int) (Math.random() * (MAX_NODES / 2));
    g.addNode(node1);
    int node2 = node1 + (int) (Math.random() * (MAX_NODES / 2));
    g.addNode(node2);

    assertTrue(!g.hasEdge(node1, node2));
    assertTrue(!g.hasEdge(node2, node1));
    // add edge between them
    g.addEdge(node1, node2);

    // remove
    assertTrue(g.removeEdge(node1, node2));
    assertTrue(!g.hasEdge(node1, node2));
    assertTrue(!g.hasEdge(node2, node1));

    // ensure nodes still exist
    assertTrue(g.hasNode(node1));
    assertTrue(g.hasNode(node2));

    // again
    assertTrue(!g.removeEdge(node1, node2));
    assertTrue(!g.hasEdge(node1, node2));
    assertTrue(!g.hasEdge(node2, node1));
  }

}
