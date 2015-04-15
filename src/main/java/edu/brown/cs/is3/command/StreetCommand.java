package edu.brown.cs.is3.command;

import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;

public class StreetCommand implements Command {
  private final String[] args;

  public StreetCommand(String[] args) {
    this.args = args;
  }

  @Override
  public Path run(MapsManager m) {
    return m.getPathByIntersections(args[0], args[1], args[2], args[3]);
  }

}
