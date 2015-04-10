package edu.brown.cs.is3.graph;

import java.util.Collections;
import java.util.List;

import edu.brown.cs.is3.maps.Node;
import edu.brown.cs.is3.maps.Way;

/**
 * @author jchen
 * Represents a path from a start node to end node, by ways, in a map graph.
 */
public class Path {
  private Node start, end;
  private List<Way> path;
  
  public Path(Node start, Node end, List<Way> path) {
    this(start, end);
    this.path = Collections.unmodifiableList(path);
  }
  
  public Path(Node start, Node end) {
    this.start = start;
    this.end = end;
  }

  public Node getStart() {
    return start;
  }

  public Node getEnd() {
    return end;
  }

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
   * Turns a list of ways into its string representation. Only takes non-null paths.
   */
  private String toStringPath() {
    StringBuilder sb = new StringBuilder();
    
    for (Way w : path) {
      sb.append(w);
      sb.append("\n");
    }

    return sb.toString();
  }
}
