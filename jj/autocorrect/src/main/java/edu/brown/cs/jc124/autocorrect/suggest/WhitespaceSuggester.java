package edu.brown.cs.jc124.autocorrect.suggest;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.jc124.util.Trie;

/**
 * @author jchen
 *
 *         Implements a suggester for splitting a word by whitespaces.
 */
public class WhitespaceSuggester implements Suggester<String> {
  private Trie<String> trie;

  /**
   * Makes a suggester that uses a trie for searching.
   *
   * @param trie
   *          the trie to use for finding prefix matches.
   */
  public WhitespaceSuggester(Trie<String> trie) {
    this.trie = trie;
  }

  @Override
  public List<Suggestion<String>> suggestions(List<String> words) {
    String lookFor = words.get(words.size() - 1);

    List<Suggestion<String>> suggest = new ArrayList<>();

    Trie<String>.TrieNode curr = trie.getRoot();
    for (int i = 0; i < lookFor.length() - 1; i++) {
      Trie<String>.TrieNode next = curr.getChild(lookFor.charAt(i));

      // if prefix not found/no children, return everything found so far
      if (next == null) {
        return suggest;
      }

      // go to next possible split of left
      curr = next;

      // check if right half is a word when left half is a word
      String right = lookFor.substring(i + 1);
      if (curr.isWord() && trie.contains(right)) {
        String split = lookFor.substring(0, i + 1) + " " + right;
        // use length of right half as weighting (meaning prefer longer left
        // halves)
        Suggestion<String> s = SuggestionFactory.createWhitespace(words, split,
            right.length());
        suggest.add(s);
      }
    }

    return suggest;
  }

}
