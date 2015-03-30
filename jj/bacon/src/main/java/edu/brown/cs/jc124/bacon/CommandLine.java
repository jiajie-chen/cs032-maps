package edu.brown.cs.jc124.bacon;

import edu.brown.cs.jc124.bacon.graph.BaconGraph;
import edu.brown.cs.jc124.util.MainRunnable;

/**
 * @author jchen
 *
 *         A class to manage and execute command line queries for Bacon.
 */
public class CommandLine implements MainRunnable {
  private BaconManager bacon;
  private String actor1;
  private String actor2;

  /**
   * Creates a command line executer that queries the given graph.
   *
   * @param bacon
   *          the manager used for querying
   * @param actor1
   *          the starting actor's name for pathfinding
   * @param actor2
   *          the ending actor's name
   */
  public CommandLine(BaconManager bacon, String actor1, String actor2) {
    this.bacon = bacon;
    this.actor1 = actor1;
    this.actor2 = actor2;
  }

  // converts a path to string output
  private String pathToString(Iterable<String> actorPath) {
    BaconGraph g = bacon.getGraph();
    StringBuilder sb = new StringBuilder();

    String prevId = null;
    for (String id : actorPath) {
      if (prevId != null) {
        sb.append(g.getNodeAttribute(prevId).getAttribute().getName());
        sb.append(" -> ");
        sb.append(g.getNodeAttribute(id).getAttribute().getName());
        sb.append(" : ");
        sb.append(g.getEdgeAttribute(prevId, id).getAttribute().getName());
        sb.append("\n");
      }

      prevId = id;
    }

    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  /**
   * runs a REPL that searches the graph and outputs to the console.
   */
  @Override
  public void runMain() {
    Iterable<String> path = bacon.query(actor1, actor2);
    if (path == null) {
      System.out.println(actor1 + " -/- " + actor2);
      return;
    }

    System.out.println(pathToString(path));
    // make sure to close repo!
    try {
      bacon.close();
    } catch (Exception e) {
      displayError("Couldn't close database file");
    }
  }

  private void displayError(String err) {
    System.out.println("ERROR: " + err);
  }
}
