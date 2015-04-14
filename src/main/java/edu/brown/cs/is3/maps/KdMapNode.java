package edu.brown.cs.is3.maps;

import edu.brown.cs.is3.cartesian.RadianLatLng;

/**
 * @author jchen A dummy version of Node used to store node positional info in a
 *         KD tree.
 */
public class KdMapNode extends RadianLatLng {
  private final String nodeId;

  /**
   * Builds a kd map node.
   * @param nodeId id of node in database.
   * @param lat of node.
   * @param lng of node.
   */
  public KdMapNode(String nodeId, Double lat, Double lng) {
    super(lat, lng);
    this.nodeId = nodeId;
  }

  /**
   * @return id of the node.
   */
  public String getId() {
    return this.nodeId;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof KdMapNode)) {
      return false;
    }

    KdMapNode n = (KdMapNode) obj;

    return this.nodeId.equals(n.nodeId);
  }

  @Override
  public int hashCode() {
    return nodeId.hashCode();
  }
}
