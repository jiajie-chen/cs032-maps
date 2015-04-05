package edu.brown.cs.is3.maps;

/**
 * Class representing way object in the database.
 * @author is3
 *
 */
public class Way {
  private final String id;
  private final String name;
  private final String startId;
  private final String endId;

  /**
   * Builds a way object from an id, a name, a start, and an end.
   * @param id of way.
   * @param name of way.
   * @param startId of way.
   * @param endId of way.
   */
  public Way(String id, String name, String startId, String endId) {
    this.id = id;
    this.name = name;
    this.startId = startId;
    this.endId = endId;
  }

  // public Way() // Maybe another constructor using actual objects npt ids

  /**
   * @return id of way.
   */
  public String getId() {
    return this.id;
  }

  /**
   * @return name of way.
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return start id of way.
   */
  public String getStartId() {
    return this.startId;
  }

  /**
   * @return end id of way.
   */
  public String getEndId() {
    return this.endId;
  }

  @Override
  public String toString() {
    return "Node: " + id + " Name: " + name + " Start: " + startId
        + " End: " + endId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Way)) {
      return false;
    }

    Way w = (Way) o;

    return this.id.equals(w.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
