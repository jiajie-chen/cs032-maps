package edu.brown.cs.jc124.autocorrect.suggest;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.jc124.data.Trie;

/**
 * @author jchen
 *
 *         Implements a suggester that looks for possible endings to a given
 *         prefix.
 */
public class PrefixSuggester implements Suggester<String> {
  private Trie<String> trie;

  /**
   * Makes a suggester that uses a trie for searching.
   *
   * @param trie
   *          the trie to use for finding prefix matches.
   */
  public PrefixSuggester(Trie<String> trie) {
    this.trie = trie;
  }

  @Override
  public List<Suggestion<String>> suggestions(List<String> words) {
    String lookFor = words.get(words.size() - 1);

    List<Suggestion<String>> suggest = new ArrayList<>();
    String accStr = "";

    Trie<String>.TrieNode curr = trie.getRoot();
    for (int i = 0; i < lookFor.length(); i++) {
      char c = lookFor.charAt(i);
      Trie<String>.TrieNode next = curr.getChild(c);
      if (next == null) {
        if (Character.isLowerCase(c)) {
          c = Character.toUpperCase(c);
          next = curr.getChild(c);
        } else {
          c = Character.toLowerCase(c);
          next = curr.getChild(c);
        }
      }

      // if prefix not found, return empty list
      if (next == null) {
        return suggest;
      }

      accStr += c;
      curr = next;
    }

    // recursively look through subtree
    gatherTrie(words, lookFor, curr, 0, suggest, accStr);
    return suggest;
  }

  // gathers all words in the sub-tree
  private void gatherTrie(List<String> words, String lookFor,
      Trie<String>.TrieNode t, int depth, List<Suggestion<String>> acc,
      String accStr) {
    // add to accumulator if word is found
    if (t.isWord()) {
      Suggestion<String> s = SuggestionFactory.createPrefix(words, accStr,
          depth);
      acc.add(s);
    }

    for (Character next : t.getEdges()) {
      gatherTrie(words, lookFor, t.getChild(next), depth + 1, acc, accStr
          + next);
    }
  }
}
