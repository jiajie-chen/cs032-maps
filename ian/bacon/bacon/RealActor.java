package edu.brown.cs.is3.bacon;

import java.util.HashSet;
import java.util.Set;

/**
 * Real implementation of actor containing relevant details.
 * @author is3
 *
 */
public class RealActor implements Actor {
  private final String id;
  private final String name;
  private boolean lookedForFilms = false;
  private Set<Film> films = new HashSet<>();

  /**
   * Constructs a real actor out of name and id.
   * @param id of actor.
   * @param name of actor.
   */
  public RealActor(String id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public Set<Film> getFilms() {
    if (!lookedForFilms) {
      this.films = Main.filmsOfActor(this);
      this.lookedForFilms = true;
    }

    return this.films;
  }

  @Override
  public void addFilm(Film film) {
    if (!lookedForFilms) {
      getFilms();
    }

    films.add(film);
  }

  @Override
  public String toString() {
    return "Name: " + name + " ID: " + id + " Films: " + films.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Actor)) {
      return false;
    }

    Actor a = (Actor) o;

    return this.getId().equals(a.getId()) && this.getName().equals(a.getName());
  }

  @Override
  public int hashCode() {
    return id.hashCode() ^ name.hashCode();
  }
}
