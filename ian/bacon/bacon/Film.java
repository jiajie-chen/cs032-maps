package edu.brown.cs.is3.bacon;

import java.util.Set;

/**
 * Requirements for all classes implementing film.
 * @author is3
 *
 */
public interface Film {

  /**
   * Returns the name of the given film.
   * @return name.
   */
  String getName();

  /**
   * Returns the id of the given film.
   * @return id.
   */
  String getId();

  /**
   * Returns a list of the stars of the film.
   * @return stars in film.
   */
  Set<Actor> getStars();

  /**
   * Adds a star to a films list of stars.
   * @param actor to add to stars.
   */
  void addStar(Actor actor);
}
