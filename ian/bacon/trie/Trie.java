package edu.brown.cs.is3.trie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.primitives.Ints;

/**
 * Implementation of trie for insertion and prefix searches.
 * @author is3
 *
 */
public class Trie implements Iterable<String> {
  private Map<Character, TrieNode> children = new HashMap<>();

  /**
   * Inner node class storing a character and an array of children.
   * @author is3
   *
   */
  private static class TrieNode {
    private Map<Character, TrieNode> children = new HashMap<>();
    private char c;
    private boolean isLeaf = false;

    public TrieNode(char c) {
      this.c = c;
    }
  }

  /**
   * Constructs a trie from a collection.
   * @param col collection of strings.
   */
  public Trie(Collection<String> col) {
    for (String word : col) {
      insert(word);
    }
  }

  /**
   * Constructs an empty trie.
   */
  public Trie() {
    this.children = new HashMap<>();
  }

  /**
   * Inserts a given string into the trie.
   * @param s the string to insert
   */
  public void insert(String s) {
    TrieNode next = null;
    TrieNode prev = null;

    if (s == null || s.equals("")) {
      return;
    }

    Pattern p = Pattern.compile("^[a-zA-Z'-\\. ]+");
    Matcher m = p.matcher(s);
    if (!m.find()) {
      return;
    }

    for (int i = 0; i < s.length(); i++) {
      char curr = s.charAt(i);
      if (i == 0) {
        next = children.get(curr);
      } else {
        prev = next;
        next = prev.children.get(curr);
      }

      if (next == null) {
        next = new TrieNode(s.charAt(i));
        if (i == 0) {
          children.put(curr, next);
        } else {
          prev.children.put(curr, next);
        }
      }
    }

    next.isLeaf = true;
    return;
  }

  /**
   * Checks if the trie contains a string.
   * @param s the string for which to look.
   * @return true if it does contain it, false otherwise.
   */
  public boolean contains(String s) {
    TrieNode next = null;
    if (s == null || s.equals("")) {
      return false;
    }

    for (int i = 0; i < s.length(); i++) {
      char curr = s.charAt(i);
      if (i == 0) {
        next = children.get(curr);
      } else {
        next = next.children.get(curr);
      }

      if (next == null) {
        return false;
      }
    }

    return next.isLeaf;
  }

  /**
   * Finds a list of prefixes of a given string.
   * @param s string for which to search.
   * @return a list of all strings in the tree for which s is a prefix.
   */
  public List<String> findPrefixes(String s) {
    TrieNode next = null;
    List<String> toReturn = new ArrayList<String>();

    if (s == null || s.equals("")) {
      for (char c : children.keySet()) {
        next = children.get(c);
        if (next != null) {
          toReturn.addAll(subPrefixSearch(next, "" + next.c));
        }
      }

      return toReturn;
    }

    for (int i = 0; i < s.length(); i++) {
      char curr = s.charAt(i);
      if (i == 0) {
        next = children.get(curr);
      } else {
        next = next.children.get(curr);
      }

      if (next == null) {
        return new ArrayList<String>();
      }
    }

    toReturn.addAll(subPrefixSearch(next, s));
    return toReturn;
  }

  /**
   * Helper procedure for searching nodes.
   * @param node current node being examined.
   * @param s the string being looked at.
   * @return a list of all strings after node for which s is a prefix.
   */
  private List<String> subPrefixSearch(TrieNode node, String s) {
    List<String> toReturn = new ArrayList<String>();
    TrieNode next;

    if (node == null) {
      return toReturn;
    } else if (node.isLeaf) {
      toReturn.add(s);
    }

    for (char c : node.children.keySet()) {
      next = node.children.get(c);
      if (next != null) {
        toReturn.addAll(subPrefixSearch(next, s + next.c));
      }
    }

    return toReturn;
  }

  /**
   * Generates a list of all words in trie within maxDist of word by LED.
   * @param word to search for
   * @param maxDist max LED away from word to search
   * @return list of all words in trie within maxDist of word
   */
  public List<String> findLed(String word, int maxDist) {
    List<String> toReturn = new ArrayList<String>();
    if (word == null || maxDist == 0) {
      return toReturn;
    }

    int[] currentRow = new int[word.length() + 1];
    for (int i = 0; i < currentRow.length; i++) {
      currentRow[i] = i;
    }

    for (char c : children.keySet()) {
      TrieNode curr = children.get(c);
      if (curr != null) {
        toReturn.addAll(
            recursiveLed(curr, curr.c, word, currentRow, maxDist, ""));
      }
    }

    return toReturn;
  }

  /**
   * Recursive helper function for findLed.
   * @param node to search from.
   * @param c character being looked at.
   * @param word being searched for.
   * @param previousRow in dynamic algorithm.
   * @param maxDist max allowable LED.
   * @param acc letters passed over so far.
   * @return list of strings after node in trie with maxDist of word.
   */
  private List<String> recursiveLed(TrieNode node, char c,
      String word, int[] previousRow, int maxDist, String acc) {
    int columns = word.length() + 1;
    int[] currentRow = new int[previousRow.length];
    currentRow[0] = previousRow[0] + 1;
    int insertCost, deleteCost, replaceCost;
    List<String> toReturn = new ArrayList<String>();
    acc += node.c;

    for (int i = 1; i < columns; i++) {
      insertCost = currentRow[i - 1] + 1;
      deleteCost = previousRow[i] + 1;

      if (word.charAt(i - 1) != c) {
        replaceCost = previousRow[i - 1] + 1;
      } else {
        replaceCost = previousRow[i - 1];
      }

      currentRow[i] = minimum(insertCost, deleteCost, replaceCost);
    }

    if (currentRow[currentRow.length - 1] <= maxDist && node.isLeaf) {
      toReturn.add(acc);
    }

    if (Ints.min(currentRow) <= maxDist) {
      for (char k : node.children.keySet()) {
        TrieNode curr = node.children.get(k);
        if (curr != null) {
          toReturn.addAll(
              recursiveLed(curr, curr.c, word, currentRow, maxDist, acc));
        }
      }
    }

    return toReturn;
  }

  /**
   * Finds the minimum of three numbers.
   * @param i first number.
   * @param j second number.
   * @param k third number.
   * @return min of i, j, and k.
   */
  private static int minimum(int i, int j, int k) {
    return Math.min(Math.min(i, j), k);
  }

  /**
   * Returns any valid splits of word into two pieces.
   * @param word to split.
   * @return a list of any possbile valid splits.
   */
  public List<String> whitespace(String word) {
    List<String> toReturn = new ArrayList<>();
    if (word == null || word.equals("")) {
      return toReturn;
    }

    for (int i = 0; i < word.length(); i++) {
      String first = word.substring(0, i);
      String second = word.substring(i, word.length());

      if (contains(first) && contains(second)) {
        toReturn.add(first + " " + second);
      }
    }

    return toReturn;
  }

  @Override
  public Iterator<String> iterator() {
    return new TrieIterator();
  }

  /**
   * Iterator for trie. Converts trie to a collection and then iterates.
   * @author is3
   *
   */
  private final class TrieIterator implements Iterator<String> {
    private Collection<String> col;
    private Iterator<String> iter;

    /**
     * Builds an iterator by converting trie into a collection.
     */
    private TrieIterator() {
      this.col = toCollection();
      this.iter = col.iterator();
    }

    @Override
    public boolean hasNext() {
      return iter.hasNext();
    }

    @Override
    public String next() {
      return iter.next();
    }

  }

  /**
   * Return this trie as a list.
   * @return a list of words in the trie.
   */
  public List<String> toList() {
    return findPrefixes("");
  }

  /**
   * Returns this trie as a collection.
   * @return a collection of words in the trie.
   */
  public Collection<String> toCollection() {
    return findPrefixes("");
  }

}
