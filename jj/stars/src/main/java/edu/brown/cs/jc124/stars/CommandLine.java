package edu.brown.cs.jc124.stars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author jchen
 *
 *         A class to manage and execute command line queries on a Star KdTree.
 */
public class CommandLine {
  private StarDataManager stars;

  /**
   * Creates a command line executer that queries the given database.
   *
   * @param stars
   *          the storage of stars to use for querying
   */
  public CommandLine(StarDataManager stars) {
    this.stars = stars;
  }

  /**
   * runs a REPL that queries the DB and outputs to the console.
   */
  public void runCli() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    String in = null;
    try {
      in = br.readLine();
    } catch (IOException e) {
      System.out.println("ERROR: can't read input stream");
    }
    while (in != null) {
      List<Star> results = cliCommand(in);

      if (results != null) {
        for (Star s : results) {
          System.out.println(s.getId());
        }
      }

      try {
        in = br.readLine();
      } catch (IOException e) {
        System.out.println("ERROR: can't read input stream");
      }
    }
  }

  private List<Star> cliCommand(String in) {
    // parse out parameters, convert to data types
    String[] params = in.trim().split("\\s+");
    // quote handling
    if (params.length >= 3 && params[2].charAt(0) == '"') {
      String quote = "";
      for (int i = 2; i < params.length; i++) {
        quote += params[i];
      }
      params = new String[] {params[0], params[1], quote};
    }

    // length check, then execute command, and capture any parsing errors
    try {
      if (params.length == 5) {
        return stars.locationCommand(params[0], params[1], params[2],
            params[3], params[4]);
      } else if (params.length == 3) {
        return stars.nameCommand(params[0], params[1], params[2]);
      } else {
        System.out.println("ERROR: incorrect number of parameters");
        return null;
      }
    } catch (NoSuchElementException | IllegalArgumentException
        | IndexOutOfBoundsException e) {
      // only errors caused by parsing or searching tree
      System.out.println("ERROR: " + e.getMessage());
      return null;
    }
  }
}
