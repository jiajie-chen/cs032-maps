package edu.brown.cs.jc124.bacon.graph;

import java.util.Collections;
import java.util.Set;

/**
 * @author jchen
 *
 *         Container to hold movie info.
 */
public class Movie {
  private String name;
  private Set<String> actorIds;

  /**
   * Makes a new movie with given info.
   *
   * @param name
   *          the name of the movie
   * @param actorIds
   *          the ids of the actors in the movie
   */
  public Movie(String name, Set<String> actorIds) {
    this.name = name;
    this.actorIds = Collections.unmodifiableSet(actorIds);
  }

  /**
   * Gets movie name.
   *
   * @return the movie name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the actor's in the movie.
   *
   * @return the ids of the actors
   */
  public Set<String> getActorIds() {
    return actorIds;
  }

}
