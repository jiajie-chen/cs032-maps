package edu.brown.cs.jc124.bacon.graph;

import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.jc124.data.GraphRepository;

/**
 * @author jchen
 *
 *         Implements a graph repository for Bacon, that stores actor and movie
 *         information.
 */
public abstract class BaconRepository implements
    GraphRepository<String, Actor, String, Movie>, AutoCloseable {
  private Map<String, Actor> internA;
  private Map<String, Movie> internM;

  /**
   * Makes a new repo.
   */
  public BaconRepository() {
    internA = new HashMap<>();
    internM = new HashMap<>();
  }

  /**
   * Get an actor by their id.
   *
   * @param id
   *          id of the actor
   * @return the actor with that id
   */
  public Actor getActor(String id) {
    Actor intern = internA.get(id);
    if (intern == null) {
      intern = getActorFromSource(id);
      internA.put(id, intern);
    }
    return intern;
  }

  /**
   * Get movie by its id.
   *
   * @param id
   *          id of the movie
   * @return the movie with that id
   */
  public Movie getMovie(String id) {
    Movie intern = internM.get(id);
    if (intern == null) {
      intern = getMovieFromSource(id);
      internM.put(id, intern);
    }
    return intern;
  }

  /**
   * Get an actor by their id, directly from the repository.
   *
   * @param id
   *          id of the actor
   * @return the actor with that id
   */
  public abstract Actor getActorFromSource(String id);

  /**
   * Get movie by its id, directly from the repository.
   *
   * @param id
   *          id of the movie
   * @return the movie with that id
   */
  public abstract Movie getMovieFromSource(String id);

  /**
   * Gets actor id by name.
   *
   * @param name
   *          the full name of the actor
   * @return the id of that actor
   */
  public abstract String getActorId(String name);

  /**
   * Gets movie id by name.
   *
   * @param name
   *          the full name of the movie
   * @return the id of that movie
   */
  public abstract String getMovieId(String name);

  @Override
  public boolean clear() {
    if (internA.isEmpty() && internM.isEmpty()) {
      return false;
    }

    internA.clear();
    internM.clear();

    return true;
  }
}
