package edu.brown.cs.is3.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.brown.cs.is3.command.Command;
import edu.brown.cs.is3.command.ReplParser;
import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;
import edu.brown.cs.jc124.traffic.TrafficManager;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

// TEST PARSING, ISDOUBLE, DB, SYSTEM
// INCLUDE MULTIPLE COMMAND, SERVER, AND LAT LNG BASED TESTING!
// SLOW TESTER MAYBE?

// TODO

// MORE TESTING
// README
// TRAFFIC
// STYLE
// SERVER STABILITY

// TODO

// TRAFFIC
// STYLE/FINDBUGS
// STREETS

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
      try {
        Command c = new ReplParser(s).parse();
        Path p = c.run(manager);
        System.out.println(p);
      } catch (IllegalArgumentException e) {
        System.err.println("ERROR: " + e.getMessage());
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

      Server s = new Server(sparkPort, db, traffic, changes);
      s.run();

      Thread t = new Thread(new TrafficManager(trafficPort, traffic, changes));
      t.start();

      // db.close();
    } catch (RuntimeException e) {
      System.err.println("ERROR: " + e.getMessage());
      return;
    } finally {
      System.out.println();
      // db.close();
    }
  }
}
