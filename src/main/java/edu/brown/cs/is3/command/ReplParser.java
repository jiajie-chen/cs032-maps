package edu.brown.cs.is3.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplParser {
  private final String s;
  private static final int EXPECTED_ARGS = 4;

  public ReplParser(String s) {
    this.s = s;
  }

  public Command parse() {
    List<String> toBuild = new ArrayList<>();

    boolean areStreetNames = Pattern.matches(
        "^\"([a-zA-Z0-9'-\\. ]+)\"\\s+\"([a-zA-Z0-9'-\\. ]+)\"\\s+"
            + "\"([a-zA-Z0-9'-\\. ]+)\"\\s+\"([a-zA-Z0-9'-\\. ]+)\"$",
        s.trim());

    if (areStreetNames) {
      Pattern p = Pattern.compile("\"([a-zA-Z0-9'-\\. ]+)\"");
      Matcher m = p.matcher(s);

      while (m.find()) {
        String arg = m.group().replace("\"", "");
        toBuild.add(arg);
      }

      if (toBuild.size() != EXPECTED_ARGS) {
        throw new IllegalArgumentException("Bad street input: " + s);
      }

      String[] args = new String[EXPECTED_ARGS];
      for (int i = 0; i < EXPECTED_ARGS; i++) {
        args[i] = toBuild.get(i);
      }

      return new StreetCommand(args);
    } else if (containsDoubles(s)) {
      String args[] = s.split("\\s+");

      if (args.length != EXPECTED_ARGS) {
        throw new IllegalArgumentException("Bad double input: " + s);
      }

      return new LatLngCommand(args);
    } else {
      throw new IllegalArgumentException("Bad input: " + s);
    }
  }

  /**
   * Checks if a list of strings contains doubles.
   * @param strings to check.
   * @return true if all strings are doubles and false otherwise.
   */
  private static boolean containsDoubles(String s) {
    String[] args = s.split("\\s+");

    for (String d : args) {
      if (!isDouble(d)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks if an individual string is a finite double.
   * @param value to check.
   * @return true if is a double and false otherwise.
   */
  private static boolean isDouble(String value) {
    try {
      double d = Double.parseDouble(value);
      if (!Double.isFinite(d)) {
        return false;
      }

      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
