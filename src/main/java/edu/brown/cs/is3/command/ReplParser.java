package edu.brown.cs.is3.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for the maps REPL.
 * @author is3
 *
 */
public class ReplParser {
  private final String s;
  private static final int EXPECTED_ARGS = 4;

  /**
   * Builds a parser for a given string.
   * @param s string to parse.
   */
  public ReplParser(String s) {
    this.s = s;
  }

  /**
   * Parses a string and throws an exception or returns a command based on user
   * input. Only takes nonempty and non null strings.
   * @return a command based on the string input by the user
   */
  public Command parse() {
    List<String> toBuild = new ArrayList<>();

    boolean areStreetNames = Pattern.matches(
        "^\"([a-zA-Z0-9'-\\. ]+)\"\\s+\"([a-zA-Z0-9'-\\. ]+)\"\\s+"
            + "\"([a-zA-Z0-9'-\\. ]+)\"\\s+\"([a-zA-Z0-9'-\\. ]+)\"$",
        s.trim()); // checks for four strings surrounded by quotes

    if (areStreetNames) {
      Pattern p = Pattern.compile("\"([a-zA-Z0-9'-\\. ]+)\"");
      Matcher m = p.matcher(s); // looks for individual strings in quotes

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
      String[] args =
          s.trim().replace("\"", "").replace("\\", "").split("\\s+");

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
    String[] args = s.trim().split("\\s+");

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
