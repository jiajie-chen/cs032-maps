package edu.brown.cs.is3.bacon;

import java.util.HashSet;
import java.util.Set;

/**
 * Real implementation of film interface.
 * @author is3
 *
 */
public class RealFilm implements Film {
  private final String id;
  private final String name;
  private boolean lookedForStars = false;
  private Set<Actor> stars = new HashSet<>();

  /**
   * Constructs a real film out of its name and id.
   * @param id of film.
   * @param name of film.
   */
  public RealFilm(String id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Set<Actor> getStars() {
    if (!lookedForStars) {
      this.stars = Main.starsOfFilm(this);
      this.lookedForStars = true;
    }

    return this.stars;
  }

  @Override
  public void addStar(Actor actor) {
    if (!lookedForStars) {
      getStars();
    }

    this.stars.add(actor);
  }

  @Override
  public String toString() {
    return "Name: " + name + " ID: " + id + " Stars: " + stars.size();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Film)) {
      return false;
    }

    Film f = (Film) o;

    return this.getId().equals(f.getId()) && this.getName().equals(f.getName());
  }

  @Override
  public int hashCode() {
    return id.hashCode() ^ name.hashCode();
  }
}
