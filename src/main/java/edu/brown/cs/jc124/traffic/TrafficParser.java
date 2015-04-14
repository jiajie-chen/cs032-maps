package edu.brown.cs.jc124.traffic;

import java.util.HashMap;
import java.util.Map;

public class TrafficParser {
  private final String s;

  public TrafficParser(String s) {
    this.s = s;
  }

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
