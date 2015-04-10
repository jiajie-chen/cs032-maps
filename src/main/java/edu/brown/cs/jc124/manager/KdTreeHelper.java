package edu.brown.cs.jc124.manager;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.maps.KdMapNode;
import edu.brown.cs.jc124.kdtree.KdTree;

/**
 * @author jchen
 * Helper class for managing a map's positional nodes by a KD tree.
 */
public class KdTreeHelper {
  private KdTree<KdMapNode> kd;
  
  /**
   * Fills the internal KdTree with nodes from the database.
   * @param db the database to fill nodes from
   */
  public void fill(Database db) {
    List<KdMapNode> allNodes = new ArrayList<>(db.allKdMapNodes());
    kd = new KdTree<>(2, allNodes);
  }
  
  /**
   * Gets the node that is closest to the point given.
   * @param lat the latitude of the point to find the closest node of
   * @param lng the longitude of the point to find the closest node of
   * @return the node closest to point (can be point, if it is in the tree)
   */
  public KdMapNode getPointClosest(Double lat, Double lng) {
    KdMapNode point = new KdMapNode("", lat, lng);
    List<KdMapNode> closest = kd.kNearestNeighbors(point, 1);
    
    return closest.get(0);
  }
}
