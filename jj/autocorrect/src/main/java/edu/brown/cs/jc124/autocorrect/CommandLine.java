package edu.brown.cs.jc124.autocorrect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import edu.brown.cs.jc124.util.MainRunnable;

/**
 * @author jchen
 *
 *         A class to manage and execute command line queries for
 *         autocorrection.
 */
public class CommandLine implements MainRunnable {
  private AutoCorrector auto;

  /**
   * Creates a command line executer that queries the given database.
   *
   * @param auto
   *          the autocorrector to use for querying
   */
  public CommandLine(AutoCorrector auto) {
    this.auto = auto;
  }

  /**
   * runs a REPL that queries the autocorrector and outputs to the console.
   */
  @Override
  public void runMain() {
    System.out.println("Ready");

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    String in = null;
    while (true) {
      try {
        in = br.readLine();
      } catch (IOException e) {
        System.out.println("ERROR: can't read input stream");
      }

      if (in == null || in.isEmpty()) {
        return;
      }

      List<String> results = AutoCorrectionParser.queryAutocorrect(auto, in);

      if (results != null) {
        for (String s : results) {
          System.out.println(s);
        }
      }

      System.out.println();
    }
  }
}
