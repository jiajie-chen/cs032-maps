package edu.brown.cs.is3.bacon;

import edu.brown.cs.is3.graph.Edge;
import edu.brown.cs.is3.graph.WeightedVertex;

/**
 * Special case of edge for actors.
 * @author is3
 *
 */
public class ActorEdge extends Edge<Actor> {
  private final Film film;

  /**
   * Builds an actor edge.
   * @param source the start vertex.
   * @param target the end vertex.
   * @param weight the edge weight.
   * @param film the film connecting the actors.
   */
  public ActorEdge(WeightedVertex<Actor> source, WeightedVertex<Actor> target,
      double weight, Film film) {

    super(source, target, weight);
    this.film = film;
  }

  /**
   * @return the film
   */
  public Film getFilm() {
    return film;
  }

  @Override
  public String toString() {
    return this.getSource().getElement().getName() + " -> "
        + this.getTarget().getElement().getName() + " : " + film.getName();
  }
}
