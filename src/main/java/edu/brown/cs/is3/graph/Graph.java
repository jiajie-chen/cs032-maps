package edu.brown.cs.is3.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.maps.Node;
import edu.brown.cs.is3.maps.Way;

// should probably be using squared distances!!!!!!!!!!!!!!!!!!!!!!!!!
// LOOK AT PATH BUILDING
/**
 * Implementation of graph and a star search for ways and nodes.
 * @author is3
 *
 */
public class Graph {
  private final Database db;
  private final Map<String, Double> traffic;
  private Node pathStart;

  /**
   * Constructs a graph out of a database to hold nodes and ways.
   * @param db allowing searches on nodes and ways.
   */
  public Graph(Database db) {
    this.db = db;
    this.traffic = null;
  }

  /**
   * Builds a traffic enabled graph from a database and a traffic map.
   * @param db allowing access to nodes and ways.
   * @param traffic mapping ways to traffic values.
   */
  public Graph(Database db, Map<String, Double> traffic) {
    this.db = db;
    this.traffic = traffic;
  }

  // g is distance so far
  // h is a heuristic
  // f is the total distance (g + h)
  /**
   * Performs a graph-based Dijkstra's on a set of nodes based on a database.
   * Uses a heuristic based on Euclidean distance.
   * @param start node to start at.
   * @param end node to end at.
   * @return path with the shortest set of ways or null list if no path exists.
   */
  public Path dijkstras(Node start, Node end) {
    if (start.equals(end)) { // MAYBE CHANGE HOW THIS IS HANDLED
      throw new RuntimeException("Those are the same node, silly!");
    }

    this.pathStart = start;

    if (traffic != null) {
      return trafficDijkstras(start, end);
    }

    Map<Node, Double> distances = new HashMap<>(); // the distances list (g)
    PriorityQueue<WeightedNode> open =
        new PriorityQueue<>(); // the open list (by f)
    Set<Node> closed = new HashSet<>(); // the explored list
    Map<Node, Way> parents = new HashMap<>(); // (node, way to node)

    distances.put(start, 0.0);
    open.add(new WeightedNode(start, 0.0));
    parents.put(start, null);

    while (!open.isEmpty()) {
      Node curr = open.poll().getEle();
      double currDistance = distances.get(curr);

      if (curr.equals(end)) {
        return generateSolution(curr, parents);
      }

      closed.add(curr);
      Set<Way> edges = db.waysOfNode(curr);

      for (Way w : edges) {
        Node next = db.nodeOfId(w.getEndId());

        if (!closed.contains(next)) {
          double nextDist = currDistance + curr.getDistance(next); // g
          double oldNextDist =
              distances.getOrDefault(next, Double.MAX_VALUE); // old g

          if (nextDist < oldNextDist) {
            double nextTotal = nextDist + next.getDistance(end); // f
            distances.put(next, nextDist);
            open.add(new WeightedNode(next, nextTotal));
            parents.put(next, w);
          }
        }
      }
    }

    return new Path(start, end); // path with no ways
  }

  /**
   * Helper procedure for Dijkstra's if there is traffic. Essentially performs a
   * lot of the same procedures. There is a lot of duplicate code, but it allows
   * us to cut down on several unnecessary calls in the inner loop if traffic is
   * not required.
   * @param start node to start from.
   * @param end node to end at.
   * @return path with the shortest set of ways or null list if no path exists.
   */
  private Path trafficDijkstras(Node start, Node end) {
    throw new RuntimeException("NYI");
    // Map<Node, Double> distances = new HashMap<>(); // the distances list (g)
    // PriorityQueue<Node> open = new PriorityQueue<>(
    // new DistanceToComparator(end, distances)); // the open list (by f)
    // Set<Node> closed = new HashSet<>(); // the explored list
    // Map<Node, Way> parents = new HashMap<>(); // (node, way to node)
    //
    // distances.put(start, 0.0);
    // open.add(start);
    // parents.put(start, null);
    //
    // while (!open.isEmpty()) {
    // Node curr = open.poll();
    //
    // if (curr.equals(end)) {
    // return generateSolution(curr, parents);
    // }
    //
    // closed.add(curr);
    //
    // for (String wayId : curr.getWayIDs()) {
    // Way w = db.wayOfId(wayId);
    // Node next = db.nodeOfId(w.getEndId());
    //
    // if (!closed.contains(next)) {
    // distances.put(next, distances.get(curr)
    // + (traffic.getOrDefault(wayId, 1.0) * curr.getDistance(next)));
    // open.add(next);
    // parents.put(next, w);
    // }
    // }
    // }

    // return new Path(start, end); // path with no ways
  }

  /**
   * Generates a solution for dijkstra's once you've reached the target.
   * @param curr target node of search.
   * @param parents map of nodes to the ways leading to them.
   * @return a list of the ways along the shortest path to curr.
   */
  private Path generateSolution(Node curr, Map<Node, Way> parents) {
    List<Way> toReturn = new ArrayList<>();
    Way toAdd = parents.get(curr);

    while (toAdd != null) {
      toReturn.add(0, toAdd);
      toAdd = parents.get(db.nodeOfId(toAdd.getStartId()));
    }

    return new Path(pathStart, curr, toReturn);
  }
}
