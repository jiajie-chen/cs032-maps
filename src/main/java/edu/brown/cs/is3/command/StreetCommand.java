package edu.brown.cs.is3.command;

import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;

public class StreetCommand implements Command {
  private final String s1, c1, s2, c2;

  public StreetCommand(String[] args) {
    this.s1 = args[0];
    this.c1 = args[1];
    this.s2 = args[2];
    this.c2 = args[3];
  }

  @Override
  public Path run(MapsManager m) {
    return m.getPathByIntersections(s1, c1, s2, c2);
  }
}
