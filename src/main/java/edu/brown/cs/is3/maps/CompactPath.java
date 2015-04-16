package edu.brown.cs.is3.maps;

import java.util.Collections;
import java.util.List;

/**
 * Compact object for sending paths to server.
 * @author is3
 *
 */
public class CompactPath {
  @SuppressWarnings("unused")
  private final List<CompactWay> path;

  /**
   * Builds
   * @param path
   */
  public CompactPath(List<CompactWay> path) {
    this.path = Collections.unmodifiableList(path);
  }
}
