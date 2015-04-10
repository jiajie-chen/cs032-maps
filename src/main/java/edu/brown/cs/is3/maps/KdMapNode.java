package edu.brown.cs.is3.maps;

/**
 * @author jchen
 * A dummy version of Node used to store node positional info in a KD tree.
 */
public class KdMapNode extends RadianLatLng {
  private String nodeId;

  public KdMapNode(String nodeId, Double lat, Double lng) {
    super(lat, lng);
    this.nodeId = nodeId;
  }

  public String getId() {
    return this.nodeId;
  }
}
