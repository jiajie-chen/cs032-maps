package edu.brown.cs.is3.maps;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

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

  public Main(String[] args) {
    this.args = args;
  }

  public static void main(String[] args) {
    Main m = new Main(args);
    m.run();

    return;
  }

  private static void printUsage() {
    System.err.println("Error: Usage: ./run [--gui] [--port <int>] <db>");
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
      processQueries();
    }
  }

  private void processQueries() {
    // TODO Auto-generated method stub

  }

  private void runSparkServer() {
    // TODO Auto-generated method stub

  }

}
