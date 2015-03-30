package edu.brown.cs.jc124.bacon.graph;

import java.util.HashSet;
import java.util.Set;

import edu.brown.cs.jc124.data.DijkstraPathFinder;
import edu.brown.cs.jc124.data.Graph;

/**
 * @author jchen
 *
 *         Extends DijkstraPathFinder to filter out by actor neighbors by
 *         actor's initials.
 */
public class BaconPathFinder extends DijkstraPathFinder<String, Actor, Movie> {

  @Override
  public Set<String> nextNodes(Graph<String, Actor, Movie> g, String current) {
    Set<String> all = super.nextNodes(g, current);
    // filter out by criterion
    Set<String> filter = new HashSet<>();
    Actor donor = g.getNodeAttribute(current).getAttribute();
    int last = donor.getName().lastIndexOf(' ');
    if (donor.getName().length() < 0) {
      return filter;
    }
    last += 1;
    char lastInitial = donor.getName().charAt(last);
    for (String id : all) {
      Actor recipient = g.getNodeAttribute(id).getAttribute();
      if (recipient.getName().length() > 0) {
        char firstInitial = recipient.getName().charAt(0);

        if (firstInitial == lastInitial) {
          filter.add(id);
        }
      }
    }
    /*
     * System.out.print("[");
     * System.out.print(g.getNodeAttribute(current).getAttribute().getName());
     * System.out.print("] "); System.out.println(); System.out.print("\t"); for
     * (String k : all) {
     * System.out.print(g.getNodeAttribute(k).getAttribute().getName());
     * System.out.print("; "); } System.out.println(); System.out.print("\t");
     * for (String k : filter) {
     * System.out.print(g.getNodeAttribute(k).getAttribute().getName());
     * System.out.print("; "); } System.out.println(); System.out.flush();
     */
    return filter;
  }
}
