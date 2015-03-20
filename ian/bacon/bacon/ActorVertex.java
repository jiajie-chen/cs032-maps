package edu.brown.cs.is3.bacon;

import java.util.Set;

import edu.brown.cs.is3.graph.Edge;
import edu.brown.cs.is3.graph.WeightedVertex;

/**
 * Special case of weighted vertex for actors.
 * @author is3
 *
 */
public class ActorVertex extends WeightedVertex<Actor> {

  /**
   * Constructs an actor vertex out of an actor and a weight.
   * @param ele actor.
   * @param weight distance of vertex.
   */
  public ActorVertex(Actor ele, double weight) {
    super(ele, weight);
  }

  @Override
  public Set<Edge<Actor>> getEdges() {
    return super.getEdges();
  }
}
