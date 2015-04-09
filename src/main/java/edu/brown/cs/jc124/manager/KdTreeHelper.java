package edu.brown.cs.jc124.manager;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.maps.Node;
import edu.brown.cs.jc124.kdtree.KdTree;

/**
 * @author jchen
 * Helper class for managing a map's node by a KD tree.
 */
public class KdTreeHelper {
  private KdTree<Node> kd;
  
  public KdTreeHelper() {
  }
  
  /**
   * Fills the internal KdTree with nodes from the database.
   * @param db the database to fill nodes from
   */
  public void fill(Database db) {
    List<Node> allNodes = new ArrayList<>(db.allNodes());
    kd = new KdTree<>(2, allNodes);
  }
  
  /**
   * Gets the node that is closest to the point given.
   * @param point the point to find the closest node of
   * @return the node closest to point (can be point, if it is in the tree)
   */
  public Node getPointClosest(Node point) {
    List<Node> closest = kd.kNearestNeighbors(point, 1);
    
    return closest.get(0);
  }
}
