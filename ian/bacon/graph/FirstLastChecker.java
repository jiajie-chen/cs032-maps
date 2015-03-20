package edu.brown.cs.is3.graph;

import edu.brown.cs.is3.bacon.Actor;

/**
 * More complicated implementation of checker for actors that checks if the
 * first letter of the first actor's first name matches the first letter of the
 * second actor's second name.
 * @author is3
 *
 */
public class FirstLastChecker implements ConnectionChecker<Actor> {

  @Override
  public boolean validConnection(Edge<Actor> edge) {
    if (edge == null || edge.getSource() == null || edge.getTarget() == null) {
      return false;
    }

    String startName = edge.getSource().getElement().getName();
    String endName = edge.getTarget().getElement().getName();

    String[] startSplit = startName.trim().split("\\s+");
    String[] endSplit = endName.trim().split("\\s+");

    if (startSplit.length == 0 || endSplit.length == 0) {
      return false;
    }

    String firstLast = startSplit[startSplit.length - 1];
    String endFirst = endSplit[0];

    if (firstLast.equals("") || endFirst.equals("")) {
      return false;
    }

    return firstLast.charAt(0) == endFirst.charAt(0);
  }

}
