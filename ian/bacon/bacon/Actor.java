package edu.brown.cs.is3.bacon;

import java.util.Set;

/**
 * Provides methods to be implemented for all actors.
 * @author is3
 *
 */
public interface Actor {
  /**
   * Returns the name of the given actor.
   * @return name of actor.
   */
  String getName();

  /**
   * Returns the id of the given actor.
   * @return id of actor.
   */
  String getId();

  /**
   * Returns list of films actor is in.
   * @return films containing actor.
   */
  Set<Film> getFilms();

  /**
   * Adds a film to the actors set of films.
   * @param film to add.
   */
  void addFilm(Film film);
}
