package edu.brown.cs.jc124.stars;

/**
 * @author jchen
 *
 *         A utility class for parsing command line queries.
 */
public final class CommandParser {
  /**
   * @author jchen
   *
   *         The type of query to execute (nearest neighbor or radius).
   */
  public enum CommandType {
    NEIGHBOR, RADIUS
  }

  private CommandParser() {
  }

  /**
   * Parses the command given as a string.
   *
   * @param sc
   *          the string that encodes the command
   * @return the type of command the string represents
   * @throws IllegalArgumentException
   *           if the string doesn't match any command
   */
  public static CommandType parseCommand(String sc) {
    if ("neighbor".equals(sc.toLowerCase())) {
      return CommandType.NEIGHBOR;
    }
    if ("radius".equals(sc.toLowerCase())) {
      return CommandType.RADIUS;
    }

    throw new IllegalArgumentException(
        "command must be 'neighbor' or 'radius'");
  }

  /**
   * Parses the k parameter of a neighbor query.
   *
   * @param sk
   *          the string encoding k
   * @return the k parameter
   * @throws NumberFormatException
   *           if sk is not a valid non-zero positive integer
   */
  public static int parseK(String sk) {
    int k;
    try {
      k = Integer.parseInt(sk);
    } catch (NumberFormatException e) {
      throw new NumberFormatException("k is not an integer");
    }

    if (k <= 0) {
      throw new NumberFormatException("k must be non-zero and positive");
    }

    return k;
  }

  /**
   * Parses the r parameter of a radius query.
   *
   * @param sr
   *          the string encoding r
   * @return the r parameter
   * @throws NumberFormatException
   *           if sr is not a valid non-zero positive number
   */
  public static double parseR(String sr) {
    double r;
    try {
      r = Double.parseDouble(sr);
    } catch (NumberFormatException e) {
      throw new NumberFormatException("r is not a number");
    }

    if (r <= 0) {
      throw new NumberFormatException("r must be non-zero and positive");
    }

    return r;
  }

  /**
   * Parses the x, y, z coordinates of a query.
   *
   * @param sx
   *          string encoding x
   * @param sy
   *          string encoding y
   * @param sz
   *          string encoding z
   * @return a 3-element array composed of: {x, y, z}
   * @throws NumberFormatException
   *           if sx, sy, or sr is not a valid number
   */
  public static double[] parseXYZ(String sx, String sy, String sz) {
    double x, y, z;
    try {
      x = Double.parseDouble(sx);
    } catch (NumberFormatException e) {
      throw new NumberFormatException("x is not a number");
    }
    try {
      y = Double.parseDouble(sy);
    } catch (NumberFormatException e) {
      throw new NumberFormatException("y is not a number");
    }
    try {
      z = Double.parseDouble(sz);
    } catch (NumberFormatException e) {
      throw new NumberFormatException("z is not a number");
    }

    return new double[] {x, y, z};
  }

  /**
   * Parses a quote-enclosed star name.
   *
   * @param sn
   *          the quote-enclosed star name
   * @return the extracted star name
   * @throws IllegalArgumentException
   *           if name is not quote enclosed
   */
  public static String parseName(String sn) {
    if (sn.length() >= 2 && sn.charAt(0) == '"'
        && sn.charAt(sn.length() - 1) == '"') {
      return sn.substring(1, sn.length() - 1).toLowerCase();
    }

    throw new IllegalArgumentException("name is not enclosed in quotes");
  }
}
