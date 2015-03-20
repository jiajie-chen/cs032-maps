package edu.brown.cs.is3.bacon;

import java.util.Set;

/**
 * Proxy implementation of actor containing only a skeleton.
 * @author is3
 *
 */
public class ActorProxy extends Proxy<Actor> implements Actor {
  private final String id;

  /**
   * Builds an actor proxy out of just an id.
   * @param id of actor.
   */
  public ActorProxy(String id) {
    this.id = id;
  }

  @Override
  public void fill() {
    if (getInternal() != null) {
      return;
    }

    if (id != null) {
      this.setInternal(Main.actorOfId(this.id));
    } else {
      this.setInternal(Main.actorOfName(this.getName()));
    }
  }

  @Override
  public String getName() {
    fill();
    return this.getInternal().getName();
  }

  @Override
  public String getId() {
    fill();
    return this.getInternal().getId();
  }

  @Override
  public Set<Film> getFilms() {
    fill();
    return this.getInternal().getFilms();
  }

  @Override
  public void addFilm(Film film) {
    fill();
    this.getInternal().addFilm(film);
  }

  @Override
  public String toString() {
    return this.getInternal().toString();
  }

  @Override
  public boolean equals(Object o) {
    fill();
    return this.getInternal().equals(o);
  }

  @Override
  public int hashCode() {
    fill();
    return this.getInternal().hashCode();
  }

}
