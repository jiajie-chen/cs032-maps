package edu.brown.cs.is3.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import edu.brown.cs.is3.cartesian.DistanceToComparator;
import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.maps.Node;
import edu.brown.cs.is3.maps.Way;

// should probably be using squared distances!!!!!!!!!!!!!!!!!!!!!!!!!
/**
 * Implementation of graph and a star search for ways and nodes.
 * @author is3
 *
 */
public class Graph {
  private final Database db;
  private Node start;

  /**
   * Constructs a graph out of a database to hold nodes and ways.
   * @param db
   */
  public Graph(Database db) {
    this.db = db;
  }

  // be wary of interesections and many streets with same names
  // ie cross of oak and maple
  // g is distance so far
  // h is a heauristic
  // f is the total distance (g + h)
  /**
   * Performs a graph-based Dijkstra's on a set of nodes based on a database.
   * Uses a heuristic based on Euclidean distance.
   * @param start node to start at.
   * @param end node to end at.
   * @return path with the shortest set of ways or null list if no path exists.
   */
  public Path dijkstras(Node start, Node end) {
    if (start.equals(end)) {
      throw new RuntimeException("Those are the same node, silly!");
    }

    this.start = start;

    Map<Node, Double> distances = new HashMap<>(); // the distances list (g)
    PriorityQueue<Node> open = new PriorityQueue<>(
        new DistanceToComparator(end, distances)); // the open list (by f)
    Map<Node, Double> closed = new HashMap<>(); // the explored distances list
    Map<Node, Way> parents = new HashMap<>(); // (node, way to node)

    distances.put(start, 0.0);
    open.add(start);
    parents.put(start, null);

    while (!open.isEmpty()) {
      Node curr = open.poll();

      if (curr.equals(end)) {
        return generateSolution(curr, parents);
      }

      closed.put(curr, distances.get(curr) + curr.getDistance(end));

      for (String wayId : curr.getWayIDs()) {
        Way w = db.wayOfId(wayId);
        Node next = db.nodeOfId(w.getEndId()); // TODO check directed graphs

        if (!closed.containsKey(next)) {
          distances.put(next, distances.get(curr) + curr.getDistance(end));
          open.add(next);
          parents.put(next, w); // make sure this whole parents thing works
        }
      }
    }

    return new Path(start, end); // path with no ways
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

    return new Path(start, curr, toReturn);
  }
}
