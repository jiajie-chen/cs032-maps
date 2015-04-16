package edu.brown.cs.is3.command;

import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;

/**
 * Command for handling graph queries between pairs of cross streets.
 * @author is3
 *
 */
public class StreetCommand implements Command {
  private final String s1, c1, s2, c2;

  private static final int FIRST_STREET = 0;
  private static final int FIRST_CROSS = 1;
  private static final int SECOND_STREET = 2;
  private static final int SECOND_CROSS = 3;

  /**
   * Builds a street command from an array of street names.
   * @param args array of street names to build from.
   */
  public StreetCommand(String[] args) {
    this.s1 = args[FIRST_STREET];
    this.c1 = args[FIRST_CROSS];
    this.s2 = args[SECOND_STREET];
    this.c2 = args[SECOND_CROSS];
  }

  @Override
  public Path run(MapsManager m) {
    return m.getPathByIntersections(s1, c1, s2, c2);
  }
}
