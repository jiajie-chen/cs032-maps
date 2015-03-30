package edu.brown.cs.jc124.autocorrect.suggest;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.jc124.data.Trie;

/**
 * @author jchen
 *
 *         Implements a suggester that uses Levenshtein edit distance to find
 *         possible corrections.
 */
public class LevenshteinSuggester implements Suggester<String> {
  private int distance;
  private Trie<String> trie;

  /**
   * Makes a suggestor that searches with a trie, with a limited edit distance.
   *
   * @param trie
   *          the trie to use for finding suggestions
   * @param distance
   *          the maximum edit distance to look for
   */
  public LevenshteinSuggester(Trie<String> trie, int distance) {
    this.trie = trie;
    this.distance = distance;
  }

  @Override
  public List<Suggestion<String>> suggestions(List<String> words) {
    String lookFor = words.get(words.size() - 1);

    int[] currDist = new int[lookFor.length() + 1];
    for (int i = 0; i < currDist.length; i++) {
      currDist[i] = i;
    }

    List<Suggestion<String>> suggest = new ArrayList<>();

    Trie<String>.TrieNode root = trie.getRoot();
    // recur over all children nodes
    for (Character next : root.getEdges()) {
      searchTrie(words, lookFor, root.getChild(next), next, currDist, suggest,
          "" + next);
    }

    return suggest;
  }

  // uses wagner-fischer algorithm to find matrix to compute distance, uses trie
  // to add row for every new letter in current branch it is searching
  private void searchTrie(List<String> words, String lookFor,
      Trie<String>.TrieNode t, char curr, int[] prevDist,
      List<Suggestion<String>> acc, String accStr) {
    int[] currDist = new int[lookFor.length() + 1];

    int min = Integer.MAX_VALUE;
    currDist[0] = prevDist[0] + 1; // empty string column
    for (int i = 1; i < currDist.length; i++) {
      if (Character.toLowerCase(lookFor.charAt(i - 1))
          == Character.toLowerCase(curr)) {
        // no need to edit
        currDist[i] = prevDist[i - 1];
      } else {
        // find min edit
        int del = prevDist[i] + 1;
        int ins = currDist[i - 1] + 1;
        int sub = prevDist[i - 1] + 1;
        currDist[i] = Math.min(del, Math.min(ins, sub));
      }

      min = Math.min(currDist[i], min);
    }

    // add to accumulator if LED is found
    if ((currDist[currDist.length - 1] <= distance) && t.isWord()) {
      Suggestion<String> s = SuggestionFactory.createLevenshtein(words, accStr,
          currDist[currDist.length - 1]);
      acc.add(s);
    }

    // continue if min LED is still below
    if (min <= distance) {
      for (Character next : t.getEdges()) {
        searchTrie(words, lookFor, t.getChild(next), next, currDist, acc,
            accStr + next);
      }
    }
  }
}
