package edu.brown.cs.is3.graph;

import java.util.Collections;
import java.util.List;

import edu.brown.cs.is3.maps.Node;
import edu.brown.cs.is3.maps.Way;

/**
 * Represents a path from a start node to end node, by ways, in a map graph.
 * @author jjchen
 *
 */
public class Path { // SHOULD MAYBE USE COMPACT WAYS
  private final Node start, end;
  private List<Way> path;

  /**
   * Builds a path from one point to another out of ways.
   * @param start node that starts path.
   * @param end node that ends path.
   * @param path list of ways that from path.
   */
  public Path(Node start, Node end, List<Way> path) {
    this(start, end);
    this.path = Collections.unmodifiableList(path);
  }

  /**
   * Builds a null path.
   * @param start node.
   * @param end node.
   */
  public Path(Node start, Node end) {
    this.start = start;
    this.end = end;
  }

  /**
   * @return start node.
   */
  public Node getStart() {
    return start;
  }

  /**
   * @return end node.
   */
  public Node getEnd() {
    return end;
  }

  /**
   * @return the list of ways.
   */
  public List<Way> getPath() {
    return path;
  }

  @Override
  public String toString() {
    if (path == null) {
      return start.getId() + " -/- " + end.getId();
    } else {
      return toStringPath();
    }
  }

  /**
   * Turns a list of ways into its string representation. Only takes non-null
   * paths.
   */
  private String toStringPath() {
    StringBuilder sb = new StringBuilder();

    for (Way w : path) {
      sb.append(w);
      sb.append("\n");
    }

    return sb.substring(0, sb.length() - 1).toString();
  }
}
