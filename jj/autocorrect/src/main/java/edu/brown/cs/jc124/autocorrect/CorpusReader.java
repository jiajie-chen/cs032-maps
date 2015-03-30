package edu.brown.cs.jc124.autocorrect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import edu.brown.cs.jc124.autocorrect.rank.NgramFrequency;
import edu.brown.cs.jc124.util.Trie;

/**
 * @author jchen Contains the method for reading a corpus and populating data
 *         from it.
 */
public final class CorpusReader {
  private CorpusReader() {
  }
  
  /**
   * Reads a corpus and populates the relevant data structures with its words.
   *
   * @param in
   *          the input file containing the corpus
   * @param trie
   *          the trie to populate with the corpus contents
   * @param bigram
   *          the bigram frequency to populate with the corpus word frequencies
   * @param unigram
   *          the unigram frequency to populate with the corpus word frequencies
   * @throws IOException
   *           if the reader couldn't read the file
   */
  public static void readCorpus(File in, Trie<String> trie,
      NgramFrequency bigram, NgramFrequency unigram) throws IOException {
    try (BufferedReader r = new BufferedReader(new FileReader(in))) {

      String line = r.readLine();
      String prev = null;
      while (line != null) {
        List<String> words = AutoCorrectionParser.cleanPhrase(line);

        for (String s : words) {
          trie.add(s);
          if (prev != null) {
            bigram.addOccurrence(prev + " " + s);
          }
          unigram.addOccurrence(s);

          prev = s;
        }

        line = r.readLine();
      }

    }
  }
}
