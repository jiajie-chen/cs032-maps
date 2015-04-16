package edu.brown.cs.jc124.manager;

import java.util.List;
import java.util.Map;

import edu.brown.cs.is3.autocorrect.SuggestionHelper;
import edu.brown.cs.is3.cartesian.RadianLatLng;
import edu.brown.cs.is3.cartesian.Tile;
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
   * Constructs a map manager to integrate the various components of maps
   * requests.
   * @param db to fill components from.
   * @param gui true if the program is being run as a gui and false otherwise.
   */
  public MapsManager(Database db, boolean gui) {
    this.db = db;

    if (gui) {
      autocorrect = new SuggestionHelper();
      autocorrect.fill(db);
    } else {
      autocorrect = null;
    }

    mapsKd = new KdTreeHelper();
    mapsKd.fill(db);
  }

  /**
   * Returns the shortest path between two nodes based on a database.
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

  /**
   * Returns the shortest path between two nodes based on a database and a map
   * of traffic through the graph.
   * @param start point.
   * @param end point.
   * @param traffic a concurrent map updated elsewhere mapping ways to traffic
   *        values.
   * @return list of ways as a path along the shortest path between the points
   *         or a path with a null list of ways if no path exists.
   */
  private Path getShortestPathTraffic(
      Node start, Node end, Map<String, Double> traffic) {

    Graph g = new Graph(db, traffic);

    Path path = g.dijkstras(start, end);
    return path;
  }

  /**
   * Returns the shortest path between two points based on a database.
   * @param lat1 lat of first point.
   * @param lng1 lng of first point.
   * @param lat2 lat of second point.
   * @param lng2 lng of second point.
   * @return list of ways as a path along the shortest path between the points
   *         or a path with a null list of ways if no path exists.
   */
  public Path getPathByPoints(
      double lat1, double lng1, double lat2, double lng2) {

    KdMapNode closestStart = mapsKd.getPointClosest(lat1, lng1);
    KdMapNode closestEnd = mapsKd.getPointClosest(lat2, lng2);

    Node start = db.nodeOfId(closestStart.getId());
    Node end = db.nodeOfId(closestEnd.getId());

    return getShortestPath(start, end);
  }

  /**
   * Alternate signal for handling traffic.
   * @param lat1 lat of first point.
   * @param lng1 lng of first point.
   * @param lat2 lat of second point.
   * @param lng2 lng of second point.
   * @param traffic a concurrent map updated elsewhere mapping ways to traffic
   *        values.
   * @return list of ways as a path along the shortest path between the points
   *         or a path with a null list of ways if no path exists.
   */
  public Path getPathByPoints(
      double lat1, double lng1, double lat2, double lng2,
      Map<String, Double> traffic) {

    KdMapNode closestStart = mapsKd.getPointClosest(lat1, lng1);
    KdMapNode closestEnd = mapsKd.getPointClosest(lat2, lng2);

    Node start = db.nodeOfId(closestStart.getId());
    Node end = db.nodeOfId(closestEnd.getId());

    return getShortestPathTraffic(start, end, traffic);
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
   * Alternately signature for handling traffic.
   * @param startStreet start corner.
   * @param startCross other start corner.
   * @param endStreet end corner.
   * @param endCross other end corner.
   * @param traffic map of ways to traffic values updated elsewhere for
   *        searching.
   * @return list of ways as a path along the shortest path between the
   *         intersection of the two streets or a path with a null list of ways
   *         if no path exists.
   */
  public Path getPathByIntersections(String startStreet, String startCross,
      String endStreet, String endCross, Map<String, Double> traffic) {

    Node start = db.nodeOfIntersection(startStreet, startCross);
    Node end = db.nodeOfIntersection(endStreet, endCross);

    return getShortestPathTraffic(start, end, traffic);
  }

  /**
   * Returns a list of suggestions for the current input.
   * @param words to auto correct.
   * @return a list of at most five words the user might mean.
   */
  public List<String> suggest(String[] words) {
    return autocorrect.suggest(words);
  }

  /**
   * Returns the tile at the given location and width.
   * @param nw the north-west corner of the tile.
   * @param width the width of the tile.
   * @return a tile at the given location with the given width.
   */
  public Tile getTile(RadianLatLng nw, double width) {
    return db.tileOfCorner(nw, width);
  }
}
