package edu.brown.cs.jc124.bacon.graph;

import java.util.Collections;
import java.util.Set;

/**
 * @author jchen
 *
 *         Container class for holding an actor's information.
 */
public class Actor {
  private String name;
  private Set<String> movieIds;

  /**
   * Makes a new actor with given info.
   *
   * @param name
   *          the name of the actor
   * @param movieIds
   *          the ids of the movies the actor has been in
   */
  public Actor(String name, Set<String> movieIds) {
    this.name = name;
    this.movieIds = Collections.unmodifiableSet(movieIds);
  }

  /**
   * Gets actor name.
   *
   * @return the actor's name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the movie ids the actor has been in.
   *
   * @return the movie ids
   */
  public Set<String> getMovieIds() {
    return movieIds;
  }
}
