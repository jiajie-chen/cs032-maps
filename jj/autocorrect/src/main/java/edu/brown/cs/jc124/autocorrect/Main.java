package edu.brown.cs.jc124.autocorrect;

import java.io.File;
import java.io.IOException;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import edu.brown.cs.jc124.autocorrect.AutoCorrector.Builder;
import edu.brown.cs.jc124.autocorrect.rank.NgramFrequency;
import edu.brown.cs.jc124.util.MainRunnable;
import edu.brown.cs.jc124.util.Trie;

/**
 * @author jchen
 *
 *         The Main class for autocorrection.
 */
public final class Main {
  /**
   * Executes the stars querier based on command line arguments.
   *
   * @param args
   *          the passed in arguments, from the command line
   */
  public static void main(String[] args) {
    OptionParser parser = new OptionParser();

    parser.accepts("gui");
    parser.accepts("smart");
    parser.accepts("prefix");
    parser.accepts("whitespace");
    OptionSpec<Integer> ledSpec = parser.accepts("led").withRequiredArg()
        .ofType(Integer.class).defaultsTo(0);
    OptionSpec<File> fileSpec = parser.nonOptions().ofType(File.class);

    OptionSet options = parser.parse(args);

    List<File> in = options.valuesOf(fileSpec);
    if (in == null || in.isEmpty()) {
      System.out.println("ERROR: Please specify a text file");
      usage();
      System.exit(1);
    }

    if (!(options.has("prefix") || options.has("whitespace")
        || options.hasArgument("led"))) {
      System.out.println("ERROR: Please specify a type of suggestion");
      usage();
      System.exit(1);
    }

    AutoCorrector auto = initAutoCorrect(options.valueOf(ledSpec),
        options.has("prefix"), options.has("whitespace"), options.has("smart"),
        in);

    MainRunnable runner;
    if (options.has("gui")) {
      runner = new SparkServer(auto, in);
    } else {
      runner = new CommandLine(auto);
    }
    runner.runMain();
  }

  private static AutoCorrector initAutoCorrect(int ledDist, boolean prefix,
      boolean whitespace, boolean smart, List<File> in) {
    NgramFrequency bigram = new NgramFrequency();
    NgramFrequency unigram = new NgramFrequency();
    Trie<String> trie = new Trie<>();

    for (File f : in) {
      try {
        CorpusReader.readCorpus(f, trie, bigram, unigram);
      } catch (IOException e) {
        System.out.println("ERROR: Moving to next file, ran into exception: " + e.getMessage());
      }
    }

    Builder b = new AutoCorrector.Builder();
    if (smart) {
      b.setSmartRanker(bigram, unigram);
    } else {
      b.setBasicRanker(bigram, unigram);
    }

    if (prefix) {
      b.addPrefixSuggester(trie);
    }
    if (whitespace) {
      b.addWhitespaceSuggester(trie);
    }
    if (ledDist > 0) {
      b.addLevenshteinSuggester(trie, ledDist);
    }

    return b.build();
  }

  private static void usage() {
    System.out
        .println("USAGE:\n"
            + "--led <number> - Activate Levenshtein Edit Distance suggestions of the given distance.\n"
            + "    A Levenshtein Edit Distance of 0 is the same thing as deactivating it.\n"
            + "--prefix - Activate prefix suggestions.\n"
            + "--whitespace - Activate splitting suggestions.\n"
            + "--smart - Activate your smart ordering.\n"
            + "--gui - Activate your GUI\n"
            + "\n"
            + "<filename> - Use the supplied file as corpus (this argument can be repeated! If there are multiple corpora, use all of the words from all of them)");
  }
  
  private Main() {
  }
}
