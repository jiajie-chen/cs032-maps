package edu.brown.cs.jc124.stars;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.jc124.util.CsvReader;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * @author jchen
 *
 *         The Main class for searching by stars.
 */
public final class Main {
  /**
   * Executes the stars querier based on command line arguments.
   *
   * @param args
   *          the passed in arguments, in the form 'program [--gui]
   *          /path/to/csv'
   */
  public static void main(String[] args) {
    OptionParser parser = new OptionParser();

    parser.accepts("gui");
    OptionSpec<File> fileSpec = parser.nonOptions().ofType(File.class);
    OptionSet options = parser.parse(args);

    File in = options.valueOf(fileSpec);
    if (in == null) {
      System.out.println("ERROR: Please specify a star CSV file");
      System.exit(1);
    }

    StarDataManager stars = init(in);
    if (options.has("gui")) {
      gui(stars, in);
    } else {
      cli(stars);
    }
  }

  private static StarDataManager init(File csv) {
    List<Star> csvData = new ArrayList<Star>();
    CsvReader cr = null;
    boolean notEof = false;
    try {
      cr = new CsvReader(csv);
      cr.setHeader(new String[] {"StarID", "ProperName", "X", "Y", "Z"});
      notEof = cr.readLine();
    } catch (IOException e) {
      System.out.println("ERROR: " + e.getMessage());
      System.exit(1);
    }

    while (notEof) {
      int id;
      String name;
      double x, y, z;

      try {
        id = Integer.parseInt(cr.get("StarID"));
        name = cr.get("ProperName");
        x = Double.parseDouble(cr.get("X"));
        y = Double.parseDouble(cr.get("Y"));
        z = Double.parseDouble(cr.get("Z"));
      } catch (NumberFormatException e) {
        System.out.println("ERROR: invalid CSV data parsed");
        continue;
      }

      csvData.add(new Star(id, name, x, y, z));

      try {
        notEof = cr.readLine();
      } catch (IOException e) {
        System.out.println("ERROR: " + e.getMessage());
        notEof = true;
      }
    }

    try {
      cr.close();
    } catch (IOException e) {
      System.out.println("ERROR: " + e.getMessage());
      System.exit(1);
    }

    return new StarDataManager(csvData);
  }

  private static void gui(StarDataManager stars, File db) {
    new SparkServer(stars, db).runSparkServer();
  }

  private static void cli(StarDataManager stars) {
    new CommandLine(stars).runCli();
  }

  private Main() {
  }
}
