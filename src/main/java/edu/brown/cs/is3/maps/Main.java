package edu.brown.cs.is3.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import spark.Spark;

/**
 * Main class implementing maps, including shortest path searches, auto
 * correction, and nearest neighbor searches based on both the command line and
 * the gui.
 * @author is3
 *
 */
public class Main implements Runnable {
  private final String[] args;
  private int sparkPort;
  private static final int DEFAULT_PORT = 3141;
  private static final int EXPECTED_ARGS = 4;

  /**
   * Constructs a main out of an array of args.
   * @param args from CLI.
   */
  public Main(String[] args) {
    this.args = args;
  }

  /**
   * Runs maps by creating and executing a main.
   * @param args from CLI.
   */
  public static void main(String[] args) {
    Main m = new Main(args);
    m.run();

    return;
  }

  /**
   * Prints program usage for CLI.
   */
  private static void printUsage() {
    System.err.println("Error: Usage: ./run [--gui] [--port <int>] <db>");
  }

  /**
   * Prints program usage for interacting with the REPL.
   */
  private static void printREPLUsage() {
    System.err
        .println("Error: Usage: "
            + "<lat1> <lon1> <lat2> <lon2> OR "
            + "<\"Street 1\"> <\"Cross Street 1\"> <\"Street 2\"> <\"Cross Street 2\">");
  }

  /**
   * Checks if a list of strings contains doubles.
   * @param strings to check.
   * @return true if all strings are doubles and false otherwise.
   */
  private static boolean containsDoubles(List<String> strings) {
    for (String s : strings) {
      if (!isDouble(s)) {
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

  @Override
  public void run() {
    OptionParser parser = new OptionParser();

    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSpec<String> dbSpec = parser.nonOptions().ofType(String.class);

    OptionSet options;

    try {
      options = parser.parse(args);
    } catch (OptionException e) {
      printUsage();
      return;
    }

    sparkPort = (int) options.valueOf("port");
    String dbPath;

    try {
      dbPath = options.valueOf(dbSpec);
    } catch (OptionException e) {
      printUsage();
      return;
    }

    if (options.has("gui")) {
      runSparkServer();
    } else {
      try {
        processQueries();
      } catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
  }

  /**
   * Launches a loop to process map queries from the command line.
   * @throws IOException on read failure.
   */
  private void processQueries() throws IOException {
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    String s = r.readLine();

    while (s != null && s.length() != 0) {
      s = r.readLine();

      OptionParser parser = new OptionParser();
      OptionSpec<String> argsSpec = parser.nonOptions().ofType(String.class);
      OptionSet options;

      try {
        options = parser.parse(args);
      } catch (OptionException e) {
        printREPLUsage();
        return;
      }

      List<String> argsList;

      try {
        argsList = options.valuesOf(argsSpec);
      } catch (OptionException e) {
        printREPLUsage();
        return;
      }

      if (argsList.size() != EXPECTED_ARGS) {
        printREPLUsage();
        return;
      } else {
        if (containsDoubles(argsList)) {
          LatLng start = new LatLng(argsList.get(0), argsList.get(1));
          LatLng end = new LatLng(argsList.get(2), argsList.get(3));
          // TODO
        } else {
          String startStreet = argsList.get(0);
          String startCross = argsList.get(1);
          String endStreet = argsList.get(2);
          String endCross = argsList.get(3);
          // TODO
        }
      }
    }

    return;
  }

  /**
   * Luanches a spark server to allow for GUI querying of maps.
   */
  private void runSparkServer() {
    Spark.setPort(sparkPort);
    Spark.externalStaticFileLocation("src/main/resources/static");
    // Spark.get("/autocorrect", new GetHandler(), new FreeMarkerEngine());
    // Spark.post("/suggestions", new SuggestionHandler());
    // TODO
  }

}
