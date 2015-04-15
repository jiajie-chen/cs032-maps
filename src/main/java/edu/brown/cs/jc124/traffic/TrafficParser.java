package edu.brown.cs.jc124.traffic;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses traffic information into a map from way ids to traffic values.
 * @author is3
 *
 */
public class TrafficParser {
  private final String s;

  /**
   * Constructs a traffic parser for a given string.
   * @param s traffic update string to parse.
   */
  public TrafficParser(String s) {
    this.s = s;
  }

  /**
   * Parses a traffic object into its key value pairs.
   * @return a map of way ids to doubles based on the traffic object.
   */
  public Map<String, Double> parse() {
    Map<String, Double> toReturn = new HashMap<>();

    String[] pairs = s.substring(1, s.length() - 1).split(", ");

    for (String pair : pairs) {
      String[] eles = pair.replace("[", "").replace("]", "").split(", ");

      String id = eles[0];
      double traffic = Double.parseDouble(eles[1]);

      toReturn.put(id, traffic);
    }

    return toReturn;
  }

}
