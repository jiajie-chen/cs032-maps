package edu.brown.cs.jc124.manager;

import edu.brown.cs.is3.autocorrect.SuggestionHelper;
import edu.brown.cs.is3.graph.Graph;
import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.maps.KdMapNode;
import edu.brown.cs.is3.maps.Node;

/**
 * @author jchen
 *
 * Manages maps and queries over maps (including auto-completion and closest-point)
 */
public class MapsManager {
  private Database db;
  private SuggestionHelper autocorrect;
  private KdTreeHelper mapsKd;
  
  /**
   * @param db
   */
  public MapsManager(Database db) {
    this.db = db;
    
    autocorrect = new SuggestionHelper();
    autocorrect.fill(db);
    
    mapsKd = new KdTreeHelper();
    mapsKd.fill(db);
  }
  
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
   * @param startStreet
   * @param startCross
   * @param endStreet
   * @param endCross
   * @return
   */
  public Path getPathByIntersections(String startStreet, String startCross, String endStreet, String endCross) {
    Node start = db.nodeOfIntersection(startStreet, startCross);
    Node end = db.nodeOfIntersection(endStreet, endCross);
    
    return getShortestPath(start, end);
  }
}
