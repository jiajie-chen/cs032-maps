package edu.brown.cs.is3.command;

import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;

public interface Command {
  Path run(MapsManager m);
}
