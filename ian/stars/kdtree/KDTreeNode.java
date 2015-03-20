package edu.brown.cs.is3.kdtree;

import edu.brown.cs.is3.cartesian.Cartesian;

/**
 * Class representing an individual node in a KDTree.
 * @author is3
 *
 * @param <T> An element of any type that extends Cartesian.
 */
class KDTreeNode<T extends Cartesian> implements Cartesian {
  private KDTreeNode<T> left;
  private KDTreeNode<T> right;
  private T ele;
  private static final double HASHING_PRIME = 31;

  /**
   * Constructs a node from an element of type Cartesian.
   * @param ele of type Cartesian with which to make the node.
   */
  public KDTreeNode(T ele) {
    this.ele = ele;
    this.setLeft(null);
    this.setRight(null);
  }

  /**
   * Returns the element held by this.
   * @return element.
   */
  public Cartesian getEle() {
    return this.ele;
  }

  @Override
  public double getX() {
    return ele.getX();
  }

  @Override
  public double getY() {
    return ele.getY();
  }

  @Override
  public double getZ() {
    return ele.getZ();
  }

  /**
   * @return the right child;
   */
  public KDTreeNode<T> getRight() {
    return right;
  }

  /**
   * @return the left child;
   */
  public KDTreeNode<T> getLeft() {
    return left;
  }

  /**
   * @param left the left child to set;
   */
  public void setLeft(KDTreeNode<T> left) {
    this.left = left;
  }

  /**
   * @param right the child to set
   */
  public void setRight(KDTreeNode<T> right) {
    this.right = right;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof KDTreeNode<?>)) {
      return false;
    }

    @SuppressWarnings("unchecked")
    KDTreeNode<Cartesian> n = (KDTreeNode<Cartesian>) o;

    return this.ele.equals(n.ele);
  }

  @Override
  public int hashCode() {
    double hash = HASHING_PRIME;
    hash *= this.getX();
    hash += this.getY();
    hash = Math.pow(hash, this.getZ());

    return (int) hash;
  }

  @Override
  public String toString() {
    return ele.toString();
  }
}
