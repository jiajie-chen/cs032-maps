package edu.brown.cs.jc124.bacon;

import java.util.List;

import edu.brown.cs.jc124.bacon.BaconManager.Builder;
import edu.brown.cs.jc124.data.RepositoryException;
import edu.brown.cs.jc124.util.MainRunnable;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * @author jchen
 *
 *         The Main class for Bacon.
 */
public final class Main {
  private static final int DEFAULT_PORT = 4567;
  /**
   * Parses command-line arguments and starts the bacon querier.
   *
   * @param args
   *          the passed in arguments, from the command line
   */
  public static void main(String[] args) {
    OptionParser parser = new OptionParser();

    parser.accepts("gui");
    OptionSpec<Integer> portSpec = parser.accepts("port").withRequiredArg()
        .ofType(Integer.class);

    OptionSet options = null;
    try {
      options = parser.parse(args);
    } catch (OptionException e) {
      exitWithError(e.getMessage(), 1);
    }

    int port = DEFAULT_PORT;
    if (options.has("port")) {
      port = options.valueOf(portSpec);
    }

    List<?> nonOpt = options.nonOptionArguments();
    Builder b = new Builder();

    MainRunnable runner;
    if (options.has("gui")) {
      if (nonOpt.size() != 1) {
        exitWithError("GUI requires only the DB file", 1);
      }

      String db = (String) nonOpt.get(0);
      runner = new SparkServer(port, b.buildDefault(db));
    } else {
      if (nonOpt.size() != 3) {
        exitWithError("CLI requires actors and the DB file", 1);
      }

      String actor1 = (String) nonOpt.get(0);
      String actor2 = (String) nonOpt.get(1);
      String db = (String) nonOpt.get(2);
      runner = new CommandLine(b.buildDefault(db), actor1, actor2);
    }
    try {
      runner.runMain();
    } catch (RepositoryException | IllegalArgumentException e) {
      exitWithError(e.getMessage(), 1);
    }
  }

  private static void printUsage() {
    System.out.println("USAGE:\n"
        + "--port <number> - the port to open the gui on (default 4567).\n"
        + "--gui - Activate the GUI\n" + "\n" + "Parameters (in order):\n"
        + "<actor 1>\n" + "<actor 2>\n" + "<filename> - SQLite database");
  }

  private static void exitWithError(String error, int exitStatus) {
    System.out.println("ERROR: " + error);
    printUsage();
    System.exit(exitStatus);
  }

  private Main() {
  }
}
