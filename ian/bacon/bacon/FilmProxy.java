package edu.brown.cs.is3.bacon;

import java.util.Set;

/**
 * Proxy implementation of film interface.
 * @author is3
 *
 */
public class FilmProxy extends Proxy<Film> implements Film {
  private final String id;

  /**
   * Builds a film proxy from an id.
   * @param id to build from.
   */
  public FilmProxy(String id) {
    this.id = id;
  }

  @Override
  public void fill() {
    if (getInternal() != null) {
      return;
    }

    this.setInternal(Main.filmOfId(this.id));
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
  public Set<Actor> getStars() {
    fill();
    return this.getInternal().getStars();
  }

  @Override
  public void addStar(Actor actor) {
    fill();
    this.getInternal().addStar(actor);
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
