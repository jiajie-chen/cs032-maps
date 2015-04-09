package edu.brown.cs.is3.autocorrect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.is3.generator.Generator;
import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.ranker.DefaultRanker;
import edu.brown.cs.is3.ranker.Ranker;
import edu.brown.cs.is3.trie.Trie;

/**
 * Manages autocorrection suggestion queries.
 *
 * @author is3
 */
public class SuggestionHelper {
  private static final int MAX_SUGGESTIONS = 5;
  
  private Trie dict;
  private Map<String, Integer> unifreqs;
  private Map<String, Integer> bifreqs;
  
  
  /**
   * Makes a suggestion helper with no data loaded.
   */
  public SuggestionHelper() {
    dict = new Trie();
    unifreqs = new HashMap<>();
    bifreqs = new HashMap<>();
  }

  /**
   * Loads way names from the database.
   *
   * @param db the database to load ways from.
   */
  public void fill(Database db) {
    for (String name : db.allWayNames()) {
      dict.insert(name);
      int count = unifreqs.containsKey(name) ? unifreqs.get(name) : 0;
      unifreqs.put(name, count + 1);
    }
  }

  /**
   * Given a list of words, returns the auto correction suggestions
   *
   * @param words
   * @return
   */
  public List<String> suggest(String[] words) {
    if (words.length == 0) {
      return new ArrayList<>();
    }

    String curr = words[words.length - 1];

    Generator g = new Generator(curr, dict);
    g.setLed(2).setPrefix(true).setWhitespace(true);

    List<String> unranked = g.generate();

    if (words.length == 0 || unranked == null || unranked.size() == 0) {
      return new ArrayList<>();
    }

    Ranker ranker;

    ranker =
        new DefaultRanker(words[words.length - 1], words, unifreqs, bifreqs);

    List<String> ranked = new ArrayList<>(unranked);
    ranked.sort(ranker);
    ranked = ranked.subList(0, Math.min(MAX_SUGGESTIONS, ranked.size()));

    return ranked;
  }

}
