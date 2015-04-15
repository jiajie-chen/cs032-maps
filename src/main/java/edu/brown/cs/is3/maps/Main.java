package edu.brown.cs.is3.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;
import edu.brown.cs.jc124.traffic.TrafficManager;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

// SHOULD BE TESTING PARSING SOMEWHERE AND MAYBE FACTOR STUFF FROM MAIN
// TURNS OUT THE DATA ENTRY IN MAPS.SQLITE3 IS THE BIGGEST NIGHTMARE EVER
// AND ONLY SORT OF GETS YOU THE KIND OF THINGS YOU WOULD WANT

// OPTIMIZATION: REMOVE IMMUTABLE, CHANGE COMPARATOR, BUILD MORE OBJECTS

// MAYBE MAKE RADIANLATLNG EXTEND SIMPLE LAT LNG FOR CONVENIENCE
// DO TONS OF TESTING AND OPTIMIZATION

// DO TESTING, COMMENTING, STYLE, TRAFFIC, DRAWING

// TEST PARSING, ISDOUBLE, DB, SYSTEM
// INCLUDE MULTIPLE COMMAND, SERVER, AND LAT LNG BASED TESTING!
// SLOW TESTER MAYBE?

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
  private int trafficPort;
  private Database db;
  private MapsManager manager;

  private static final int DEFAULT_PORT = 3141;
  private static final int DEFAULT_TRAFFIC = 3142;
  private static final int EXPECTED_ARGS = 4;

  private static final int ZEROETH = 0;
  private static final int FIRST = 1;
  private static final int SECOND = 2;
  private static final int THIRD = 3;

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
    System.err.println(
        "ERROR: Usage: ./run [--gui] [--port <int>] [--tport <int>] <db>");
  }

  /**
   * Prints program usage for interacting with the REPL.
   */
  private static void printREPLUsage() {
    System.err.println("ERROR: Usage: "
        + "<lat1> <lon1> <lat2> <lon2> OR "
        + "<\"Street 1\"> <\"Cross Street 1\"> "
        + "<\"Street 2\"> <\"Cross Street 2\">");
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
    parser.accepts("tport").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_TRAFFIC);
    OptionSpec<String> dbSpec = parser.nonOptions().ofType(String.class);

    OptionSet options;
    String dbPath;

    try {
      options = parser.parse(args);
      sparkPort = (int) options.valueOf("port");
      trafficPort = (int) options.valueOf("tport");
      dbPath = options.valueOf(dbSpec);
    } catch (OptionException e) {
      printUsage();
      return;
    }

    if (dbPath == null || dbPath.isEmpty()) {
      printUsage();
      return;
    }

    try {
      db = new Database(dbPath);
    } catch (ClassNotFoundException | SQLException e) {
      System.err.println("ERROR: " + e.getMessage());
      return;
    }

    if (options.has("gui")) {
      runSparkServer();
    } else {
      try {
        manager = new MapsManager(db, false);
      } catch (RuntimeException e) {
        System.err.println("ERROR: " + e.getMessage());
        return;
      }

      try {
        processQueries();
      } catch (IOException | RuntimeException e) {
        System.err.println("ERROR: " + e.getMessage());
        db.close();
        return;
      }
    }
  }

  /**
   * Launches a loop to process map queries from the command line.
   * @throws IOException on read failure.
   */
  private void processQueries() throws IOException {
    BufferedReader r =
        new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
    String s = r.readLine();

    while (s != null && s.length() != 0) {
      OptionParser parser = new OptionParser();
      OptionSpec<String> argsSpec = parser.nonOptions().ofType(String.class);
      OptionSet options;

      try { // maybe needs checks for "" or regexes somewhere!!!!!!!
        options = parser.parse(s.split("[ ]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
      } catch (OptionException e) {
        printREPLUsage();
        db.close();
        return;
      }

      List<String> argsList;

      try {
        argsList = options.valuesOf(argsSpec);
      } catch (OptionException e) {
        printREPLUsage();
        db.close();
        return;
      }

      if (argsList.size() != EXPECTED_ARGS) {
        printREPLUsage();
        db.close();
        return;
      } else {
        Path path = null;

        if (containsDoubles(argsList)) {
          Double lat1 = Double.parseDouble(argsList.get(ZEROETH));
          Double lng1 = Double.parseDouble(argsList.get(FIRST));

          Double lat2 = Double.parseDouble(argsList.get(SECOND));
          Double lng2 = Double.parseDouble(argsList.get(THIRD));

          path = manager.getPathByPoints(lat1, lng1, lat2, lng2);
        } else { // maybe needs checks for "" or regexes somewhere!!!!!!!
          String startStreet = argsList.get(ZEROETH).replace("\"", "");
          String startCross = argsList.get(FIRST).replace("\"", "");

          String endStreet = argsList.get(SECOND).replace("\"", "");
          String endCross = argsList.get(THIRD).replace("\"", "");

          path = manager.getPathByIntersections(startStreet, startCross,
              endStreet, endCross);
        }

        System.out.println(path);
      }

      s = r.readLine();
    }

    db.close();
    return;
  }

  /**
   * Luanches a spark server to allow for GUI querying of maps.
   */
  private void runSparkServer() {
    try {
      Map<String, Double> traffic = new ConcurrentHashMap<>();
      Map<String, Double> changes = new ConcurrentHashMap<>();

      Thread t = new Thread(new TrafficManager(trafficPort, traffic, changes));
      // t.start();

      Server s = new Server(sparkPort, db, traffic, changes);
      s.run();
      // db.close();
    } catch (RuntimeException e) {
      System.err.println(e.getMessage());
      return;
    } finally {
      db.close();
    }
  }
}
