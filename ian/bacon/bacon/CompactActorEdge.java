package edu.brown.cs.is3.bacon;

/**
 * Class for sending results to the front end compactly.
 * @author is3
 *
 */
public final class CompactActorEdge {
  @SuppressWarnings("unused")
  private final String firstActorName;
  @SuppressWarnings("unused")
  private final String firstActorId;
  @SuppressWarnings("unused")
  private final String secondActorName;
  @SuppressWarnings("unused")
  private final String secondActorId;
  @SuppressWarnings("unused")
  private final String filmName;
  @SuppressWarnings("unused")
  private final String filmId;

  /**
   * Builds a compact actor edge out of an actor edge to avoid long or infinite
   * object chains when sending JSON.
   * @param ae ActorEdge to compress.
   * @return a CAE containing the information from ae.
   */
  public static CompactActorEdge toCompactEdge(ActorEdge ae) {
    return new CompactActorEdge(ae.getSource().getElement(), ae.getTarget()
        .getElement(), ae.getFilm());
  }

  /**
   * Builds a compact actor edge out of two actors and a film.
   * @param first actor.
   * @param second actor.
   * @param film a film.
   */
  private CompactActorEdge(Actor first, Actor second, Film film) {
    this.firstActorName = first.getName();
    this.firstActorId = first.getId();
    this.secondActorName = second.getName();
    this.secondActorId = second.getId();
    this.filmName = film.getName();
    this.filmId = film.getId();
  }

}
