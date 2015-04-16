package edu.brown.cs.is3.command;

import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;

public interface Command {
  /**
   * Executes a path search command.
   * @param m maps manager to aid in execution.
   * @return the path between the nodes specified in the command.
   */
  Path run(MapsManager m);
}
