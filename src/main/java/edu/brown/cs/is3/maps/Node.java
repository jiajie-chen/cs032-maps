package edu.brown.cs.is3.maps;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Class representing a node object in the database;
 * @author is3
 *
 */
public class Node {
  private final String id;
  private final RadianLatLng pos;
  private Set<String> wayIDs = new HashSet<>(); // maybe use actual objects

  /**
   * Builds a node from an id and a pos.
   * @param id of node;
   * @param pos of node;
   */
  public Node(String id, RadianLatLng pos) {
    this.id = id;
    this.pos = pos;
  }

  /**
   * Builds a node from an id, a lat, and a lng.
   * @param id of node.
   * @param lat of node.
   * @param lng of node.
   */
  public Node(String id, double lat, double lng) {
    this.id = id;
    this.pos = new RadianLatLng(lat, lng);
  }

  /**
   * @return the id of the node.
   */
  public String getId() {
    return this.id;
  }

  /**
   * @return the pos of the node.
   */
  public RadianLatLng getPos() {
    return this.pos;
  }
  

  /**
   * Finds the distance along the surface of the globe from this node to another
   * node.
   * @param end other node to move towards.
   * @return distance along the globe between this and end.
   */
  public double getDistance(Node end) {
    return this.getPos().distance(end.getPos());
  }

  /**
   * @return an immutable list of the ids of the way connected to this node.
   */
  public Set<String> getWayIDs() {
    return ImmutableSet.copyOf(this.wayIDs); // maybe should get rid of copy
                                             // for performance
  }

  /**
   * Adds a way to this nodes set of ways.
   * @param w
   */
  public void addWay(Way w) {
    wayIDs.add(w.getId());
  }

  @Override
  public String toString() {
    return "Node: " + id + " Location: " + pos + " Ways: " + wayIDs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Node)) {
      return false;
    }

    Node n = (Node) o;

    return this.getId().equals(n.getId());
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
