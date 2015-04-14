package edu.brown.cs.jc124.manager;

import java.util.List;

import edu.brown.cs.is3.autocorrect.SuggestionHelper;
import edu.brown.cs.is3.graph.Graph;
import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.maps.KdMapNode;
import edu.brown.cs.is3.maps.Node;

/**
 * @author jchen Manages maps and queries over maps (including autocompletion
 *         and closest-point)
 */
public class MapsManager {
  private final Database db;
  private final SuggestionHelper autocorrect;
  private final KdTreeHelper mapsKd;

  /**
   * @param db to fill components from.
   */
  public MapsManager(Database db) {
    this.db = db;

    autocorrect = new SuggestionHelper();
    autocorrect.fill(db);

    mapsKd = new KdTreeHelper();
    mapsKd.fill(db);
  }

  /**
   * Returns the shortest path between two points based on a database.
   * @param start point.
   * @param end point.
   * @return list of ways as a path along the shortest path between the points
   *         or a path with a null list of ways if no path exists.
   */
  private Path getShortestPath(Node start, Node end) {
    Graph g = new Graph(db);

    Path path = g.dijkstras(start, end);
    return path;
  }

  public Path getPathByPoints(Double lat1, Double lng1, Double lat2, Double lng2) {
    KdMapNode closestStart = mapsKd.getPointClosest(lat1, lng1);
    KdMapNode closestEnd = mapsKd.getPointClosest(lat2, lng2);

    Node start = db.nodeOfId(closestStart.getId());
    Node end = db.nodeOfId(closestEnd.getId());

    return getShortestPath(start, end);
  }

  /**
   * @param startStreet start corner.
   * @param startCross other start corner.
   * @param endStreet end corner.
   * @param endCross other end corner.
   * @return list of ways as a path along the shortest path between the
   *         intersection of the two streets or a path with a null list of ways
   *         if no path exists.
   */
  public Path getPathByIntersections(String startStreet, String startCross,
      String endStreet, String endCross) {
    Node start = db.nodeOfIntersection(startStreet, startCross);
    Node end = db.nodeOfIntersection(endStreet, endCross);

    return getShortestPath(start, end);
  }

  /**
   * Returns a list of suggestions for the current input.
   * @param words to auto correct.
   * @return a list of at most five words the user might mean.
   */
  public List<String> suggest(String[] words) {
    return autocorrect.suggest(words);
  }
}
