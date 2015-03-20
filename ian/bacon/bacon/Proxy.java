package edu.brown.cs.is3.bacon;

/**
 * Class to make a proxy to be filled in later.
 * @author is3
 * @param <E> class to imitate.
 */
public abstract class Proxy<E> {
  private E internal = null; // internal node storing true data

  /**
   * Fills the internal node with an actual representation.
   */
  public abstract void fill();

  /**
   * @return the internal representation.
   */
  public E getInternal() {
    return internal;
  }

  /**
   * @param internal the internal representation to set.
   */
  public void setInternal(E internal) {
    this.internal = internal;
  }
}
