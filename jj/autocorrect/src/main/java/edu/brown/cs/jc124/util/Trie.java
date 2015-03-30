package edu.brown.cs.jc124.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jchen
 *
 *         Implements a collection that stores a sequence of characters as a
 *         trie.
 *
 * @param <T>
 *          the sequence of characters to store within the trie
 */
public class Trie<T extends CharSequence> extends AbstractCollection<T> {
  /**
   * @author jchen
   *
   *         A representation of a trie's node.
   */
  public final class TrieNode {
    private Map<Character, TrieNode> children;
    private boolean word;

    private TrieNode() {
      this.word = false;
      children = new HashMap<>();
    }

    /**
     * Shows whether or not this node is the end node for a word.
     *
     * @return whether or not this node is the end node for a word
     */
    public boolean isWord() {
      return word;
    }

    /**
     * Shows whether or not this node has children.
     *
     * @return whether or not this node has children
     */
    public boolean hasChildren() {
      return (children != null) && (children.size() > 0);
    }

    /**
     * Gets the child of the node, of a given letter.
     *
     * @param key
     *          the letter/edge to get the child node
     * @return the child node at that edge, or null if none
     */
    public TrieNode getChild(char key) {
      return children.get(key);
    }

    /**
     * Gives the outgoing edges/letters from this node.
     *
     * @return the outgoing edges/letters from this node
     */
    public Collection<Character> getEdges() {
      return Collections.unmodifiableCollection(children.keySet());
    }
  }

  private TrieNode root;

  /**
   * Makes a new trie for storing sequences.
   */
  public Trie() {
    root = new TrieNode();
  }

  /**
   * Gets the root node of the trie, for external traversal.
   *
   * @return the root node
   */
  public TrieNode getRoot() {
    return root;
  }

  @Override
  public int size() {
    return sizeHelper(root);
  }

  private int sizeHelper(TrieNode curr) {
    int size = 0;

    // only contribute to size if it is a word
    if (curr.isWord()) {
      size++;
    }
    // recursively find child's sizes
    for (TrieNode child : curr.children.values()) {
      size += sizeHelper(child);
    }

    return size;
  }

  @Override
  public boolean contains(Object o) {
    if (!(o instanceof CharSequence)) {
      return false;
    }

    CharSequence phrase = (CharSequence) o;

    // empty string
    if (phrase.length() < 1) {
      return false;
    }
    TrieNode curr = root;
    for (int i = 0; i < phrase.length(); i++) {
      TrieNode next = curr.getChild(phrase.charAt(i));

      // if prefix not found, return false
      if (next == null) {
        return false;
      }

      curr = next;
    }

    return curr.isWord();
  }

  @Override
  public boolean add(T phrase) {
    // empty string
    if (phrase.length() < 1) {
      return false;
    }

    TrieNode curr = root;
    // insert all characters into tree
    for (int i = 0; i < phrase.length(); i++) {
      // check if child exists, and add new node if it doesn't
      char c = phrase.charAt(i);
      TrieNode child = curr.children.get(c);
      if (child == null) {
        child = new TrieNode();
        curr.children.put(c, child);
      }

      // then continue down tree
      curr = child;
    }

    boolean old = curr.word;
    // set final node's word flag to true
    curr.word = true;
    // if old flag was false, then node was modified
    return !old;
  }

  @Override
  public boolean remove(Object o) {
    if (!(o instanceof CharSequence)) {
      return false;
    }

    CharSequence phrase = (CharSequence) o;

    // empty string
    if (phrase.length() < 1) {
      return false;
    }

    TrieNode curr = root;
    TrieNode last = root;
    char lastC = phrase.charAt(0);
    for (int i = 0; i < phrase.length(); i++) {
      // check if child exists
      char c = phrase.charAt(i);
      TrieNode child = curr.children.get(c);

      if (child == null) {
        return false; // string isn't in trie
      } else if (child.isWord() && i != phrase.length() - 1) {
        last = child; // document last word found before end
        lastC = c;
      }

      // then continue down tree
      curr = child;
    }

    // not a word in the trie
    if (!curr.isWord()) {
      return false;
    }

    if (curr.hasChildren()) {
      curr.word = false;
    } else {
      last.children.remove(lastC);
    }
    return true;
  }

  @Override
  public Iterator<T> iterator() {
    throw new UnsupportedOperationException();
  }
}
