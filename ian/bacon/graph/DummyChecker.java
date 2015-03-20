package edu.brown.cs.is3.graph;

/**
 * Dummy checker that always returns edges are valid.
 * @author is3
 *
 * @param <E> generic type of dummy checker.
 */
public class DummyChecker<E> implements ConnectionChecker<E> {

  @Override
  public boolean validConnection(Edge<E> edge) {
    return true;
  }

}
