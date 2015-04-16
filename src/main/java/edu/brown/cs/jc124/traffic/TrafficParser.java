package edu.brown.cs.jc124.traffic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    Type listType =
        new TypeToken<ArrayList<ArrayList<String>>>() {
        }.getType();
    List<List<String>> pairs = new Gson().fromJson(s, listType);

    for (List<String> eles : pairs) {
      String id = eles.get(0);
      double traffic = Double.parseDouble(eles.get(1));

      toReturn.put(id, traffic);
    }

    return toReturn;
  }

}
