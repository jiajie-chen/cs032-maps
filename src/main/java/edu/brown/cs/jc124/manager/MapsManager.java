package edu.brown.cs.jc124.manager;

import java.util.List;

import edu.brown.cs.is3.autocorrect.SuggestionHelper;
import edu.brown.cs.is3.graph.Graph;
import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.maps.KdMapNode;
import edu.brown.cs.is3.maps.Node;
import edu.brown.cs.is3.maps.Way;

/**
 * @author jchen
 *
 * Manages maps and queries over maps (including autocompletion and closest-point)
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
  
  private List<Way> getShortestPath(Node start, Node end) {
    Graph g = new Graph(db);
    List<Way> path = null;
    
    path = g.dijkstras(start, end);
    return path;
  }
  
  public List<Way> getPathByPoints(Double lat1, Double lng1, Double lat2, Double lng2) {
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
  public List<Way> getPathByIntersections(String startStreet, String startCross, String endStreet, String endCross) {
    Node start = db.nodeOfIntersection(startStreet, startCross);
    Node end = db.nodeOfIntersection(endStreet, endCross);
    
    return getShortestPath(start, end);
  }
}
