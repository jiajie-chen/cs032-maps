package Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.maps.Node;
import edu.brown.cs.is3.maps.Way;

// should probably be using squared distances!!!!!!!!!!!!!!!!!!!!!!!!!
public class Graph {
  private Database db;

  public Graph(Database db) {
    this.db = db;
  }

  // be wary of interesections and many streets with same names
  // ie cross of oak and maple
  // g is distance so far
  // h is a heauristic
  // f is the total distance (g + h)
  public List<Way> dijkstras(Node start, Node end) {
    PriorityQueue<Node> open = new PriorityQueue<>(); // the open list (by f)
    Map<Node, Double> distances = new HashMap<>(); // the distances list (g)
    Map<Node, Double> closed = new HashMap<>(); // the explored distances list
    Map<Node, Way> parents = new HashMap<>(); // (node, parent) // MAYBE WAY?

    distances.put(start, 0.0);
    open.add(start);
    parents.put(start, null);

    while (!open.isEmpty()) {
      Node curr = open.poll();

      if (curr.equals(end)) {
        return generateSolution(curr);
      }

      closed.put(curr, distances.get(curr) + curr.getDistance(end));

      for (String wayId : curr.getWayIDs()) {
        Way w = db.wayOfId(wayId);
        Node next;

        if (curr.equals(w.getStartId())) { // this is all messed up ahhhhhhhhhh
          next = db.nodeOfId(w.getEndId()); // ABSTRACT THIS
        } else if (curr.equals(w.getEndId())) { // curr is the end of w
          next = db.nodeOfId(w.getStartId());
        } else {
          throw new RuntimeException(curr + " isn't part of of " + w + "!");
        }

        if (!closed.containsKey(next)) {
          distances.put(next, distances.get(curr) + curr.getDistance(end));
          open.add(next);
          parents.put(next, w); // make sure this whole parents thing works
        }
      }
    }
  }

  private List<Way> generateSolution(Node curr) {
    // TODO Auto-generated method stub
    return null;
  }
}
