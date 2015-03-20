package edu.brown.cs.is3.kdtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.MinMaxPriorityQueue;

import edu.brown.cs.is3.cartesian.Cartesian;
import edu.brown.cs.is3.cartesian.CompareDistanceFrom;
import edu.brown.cs.is3.cartesian.CompareX;
import edu.brown.cs.is3.cartesian.CompareY;
import edu.brown.cs.is3.cartesian.CompareZ;

/**
 * An implementation of KDTrees in three dimensions based on any type that
 * implements Cartesian. Builds the tree and then supports queries based
 * on radius and nearest neighbor.
 * @author is3
 *
 * @param <T> a type that implements Cartesian.
 */
public class KDTree<T extends Cartesian> {
  private KDTreeNode<Cartesian> root;
  public static final int NUM_DIMENSIONS = 3;

  /**
   * Constructs a KDTree out of a collection of Cartesian using a helper.
   * @param starCol the collection of Cartesian from which to make the tree.
   */
  public KDTree(Collection<Cartesian> starCol) {
    if (starCol.size() == 0) {
      root = null;
      return;
    }

    List<Cartesian> starList = new ArrayList<Cartesian>(starCol);

    root = buildKDTree(starList, 0);
  }

  /**
   * Private helper to set up a newly built KDTree and subtrees.
   * @param starList collection of Cartesian to use in list form.
   * @param depth current depth of tree to determine which dimension to use.
   */
  private KDTreeNode<Cartesian> buildKDTree(
      List<Cartesian> starList,
      int depth) {
    Comparator<Cartesian> comp = getComp(depth);

    if (starList.size() == 0) {
      return null;
    }

    if (starList.size() == 1) {
      return new KDTreeNode<Cartesian>(starList.get(0));
    }

    List<Cartesian> lessList = new ArrayList<Cartesian>();
    List<Cartesian> greaterEqList = new ArrayList<Cartesian>();
    Cartesian curr;

    Cartesian median = median(starList, depth);
    KDTreeNode<Cartesian> medianNode = new KDTreeNode<Cartesian>(median);
    starList.remove(median);

    Iterator<Cartesian> iter = starList.iterator();

    while (iter.hasNext()) {
      curr = iter.next();

      if (comp.compare(median, curr) < 0) {
        lessList.add(curr);
      } else if (comp.compare(median, curr) >= 0) {
        greaterEqList.add(curr);
      }

    }

    medianNode.setLeft(buildKDTree(lessList, depth + 1));
    medianNode.setRight(buildKDTree(greaterEqList, depth + 1));

    return medianNode;
  }

  /**
   * Returns the median of a previously unsorted list of cartesians.
   * @param l the list to search.
   * @param depth the current depth for sorting purposes.
   * @return the median of l sorted by the current dimension.
   */
  private static Cartesian median(List<Cartesian> l, int depth) {
    Comparator<Cartesian> comp = getComp(depth);
    l.sort(comp);

    if (l.size() == 0) {
      return null;
    } else if (l.size() == 1) {
      return l.get(0);
    } else {
      return l.get(l.size() / 2);
    }
  }

  /**
   * Returns an appropriate comparator based on the dimension currently
   * being examined.
   * @param depth the current depth being looked at.
   * @return an appropriate comparator for Cartesian at that depth.
   */
  private static Comparator<Cartesian> getComp(int depth) {
    Comparator<Cartesian> comp = null;

    if (depth < 0) {
      System.out.println("ERROR: Depth must be non-negative.");
      throw new IllegalArgumentException();
    } else if ((depth % NUM_DIMENSIONS) == 0) {
      comp = new CompareX();
    } else if ((depth % NUM_DIMENSIONS) == 1) {
      comp = new CompareY();
    } else if ((depth % NUM_DIMENSIONS) == 2) {
      comp = new CompareZ();
    }

    return comp;
  }

  /**
   * Returns a list of the k nearest neighbors of pos in KDTree.
   * @param k an integer number of neighbors to return.
   * @param pos a Cartesian value around which to search.
   * @return the k largest neighbors of pos.
   */
  public List<Cartesian> nearestNeighbors(int k, Cartesian pos) {
    if (k < 0) {
      System.out.println("ERROR: K must be at least zero.");
      return new ArrayList<Cartesian>();
    }
    if (k == 0) {
      return new ArrayList<Cartesian>();
    }

    MinMaxPriorityQueue<Cartesian> bpq = MinMaxPriorityQueue
        .orderedBy(new CompareDistanceFrom(pos))
        .maximumSize(k).expectedSize(k).create();

    neighborSearch(k, pos, bpq, root, 0);
    List<Cartesian> toReturn = new ArrayList<Cartesian>();

    while (!bpq.isEmpty()) {
      toReturn.add(bpq.pollFirst());
    }

    return toReturn;
  }

  /**
   * Helper method for nearestNeighbor that recursively searches both
   * subtrees until no closer elements could be found.
   * @param k the number of neighbors to find.
   * @param pos a Cartesian value around which to search.
   * @param bpq bounded priority queue holding the nearest Cartesian found yet.
   * @param curr the current node being examine.
   * @param depth the depth of the current node.
   */
  private void neighborSearch(
      int k, Cartesian pos, MinMaxPriorityQueue<Cartesian> bpq,
      KDTreeNode<Cartesian> curr, int depth) {

    if (curr == null) {
      return;
    }

    Comparator<Cartesian> comp = getComp(depth);
    bpq.add(curr.getEle());

    if (comp.compare(pos, curr) > 0) {
      neighborSearch(k, pos, bpq, curr.getLeft(), depth + 1);
    } else if (comp.compare(pos, curr) <= 0) {
      neighborSearch(k, pos, bpq, curr.getRight(), depth + 1);
    }

    if (
        bpq.size() < k
        || curr.getDimensionalDistance(pos, depth)
        < bpq.peekLast().getDistance(pos)) {

      if (comp.compare(pos, curr) > 0) {
        neighborSearch(k, pos, bpq, curr.getRight(), depth + 1);
      } else if (comp.compare(pos, curr) <= 0) {
        neighborSearch(k, pos, bpq, curr.getLeft(), depth + 1);
      }
    }

    return;
  }

  /**
   * Returns a list of all points within r of pos in KDTree.
   * @param r the radius to search within.
   * @param pos the position around which to search.
   * @return a list of all points within r of pos.
   */
  public List<Cartesian> radiusSearch(double r, Cartesian pos) {
    if (r < 0) {
      System.out.println("ERROR: R must be a nonnegative number.");
      return new ArrayList<>();
    }

    MinMaxPriorityQueue<Cartesian> bpq = MinMaxPriorityQueue
        .orderedBy(new CompareDistanceFrom(pos)).create();

    radiusHelper(r, pos, bpq, root, 0);
    List<Cartesian> toReturn = new ArrayList<Cartesian>();

    while (!bpq.isEmpty()) {
      toReturn.add(bpq.pollFirst());
    }

    return toReturn;

  }

  /**
   * Helper method for radiusSearch that recursively searches subtrees until no
   * points closer than r units away remain.
   * @param r the radius around pos to search.
   * @param pos the point around which to search.
   * @param bpq the priority queue holding the Cartesian found so far.
   * @param curr the current node being examined.
   * @param depth the depth of the current node.
   */
  private void radiusHelper(
      double r, Cartesian pos, MinMaxPriorityQueue<Cartesian> bpq,
      KDTreeNode<Cartesian> curr, int depth) {

    if (curr == null) {
      return;
    }

    Comparator<Cartesian> comp = getComp(depth);
    if (curr.getDistance(pos) <= r) {
      bpq.add(curr.getEle());
    }

    if (comp.compare(pos, curr) > 0) {
      radiusHelper(r, pos, bpq, curr.getLeft(), depth + 1);
    } else if (comp.compare(pos, curr) <= 0) {
      radiusHelper(r, pos, bpq, curr.getRight(), depth + 1);
    }

    if (curr.getDimensionalDistance(pos, depth) < r) {

      if (comp.compare(pos, curr) > 0) {
        radiusHelper(r, pos, bpq, curr.getRight(), depth + 1);
      } else if (comp.compare(pos, curr) <= 0) {
        radiusHelper(r, pos, bpq, curr.getLeft(), depth + 1);
      }
    }

    return;

  }
  /**
   * Returns true if t is a valid tree and false otherwise.
   * @param t the tree to be tested.
   * @return true if valid and false otherwise.
   */
  public static boolean isValidTree(KDTree<Cartesian> t) {
    if (t == null) {
      return true;
    }
    return isValidTree(0, t.root);
  }
  /**
   * Checks if the current tree or subtree is valid.
   * @param depth the depth of the node being examined.
   * @param curr the current node being looked at.
   * @return true if curr is the root of a valid tree and false otherwise.
   */
  private static boolean isValidTree(int depth, KDTreeNode<Cartesian> curr) {
    Comparator<Cartesian> comp = getComp(depth);

    if (curr == null || (curr.getLeft() == null && curr.getRight() == null)) {
      return true;
    }

    if (!(comp.compare(curr, curr.getLeft()) < 0
        && comp.compare(curr, curr.getRight()) >= 0)) {
      System.out.println(depth + " CURR: " + curr + " LEFT: "
          + curr.getLeft() + " RIGHT: " + curr.getRight());
      return false;
    } else {
      return isValidTree(depth + 1, curr.getLeft())
          && isValidTree(depth + 1, curr.getRight());
    }
  }
  /**
   * A slow version of nearest neighbor that iterates through every star in the
   * collection.
   * @param k the number of neighbors to find.
   * @param pos the Cartesian around which to look.
   * @param starCol the collection to search.
   * @return a list of up to k of the nearest neighbors of pos from starCol.
   */
  protected static List<Cartesian> slowNeigh(
      int k, Cartesian pos,
      List<Cartesian> starCol) {
    if (k <= 0) {
      System.out.println("ERROR: K must be at least one.");
      return new ArrayList<>();
    }
    MinMaxPriorityQueue<Cartesian> bpq = MinMaxPriorityQueue
        .orderedBy(new CompareDistanceFrom(pos))
        .maximumSize(k).expectedSize(k).create();
    Iterator<Cartesian> iter = starCol.iterator();
    while (iter.hasNext()) {
      Cartesian curr = iter.next();
      bpq.add(curr);
    }
    List<Cartesian> toReturn = new ArrayList<Cartesian>();
    while (!bpq.isEmpty()) {
      toReturn.add(bpq.pollFirst());
    }
    return toReturn;
  }
  /**
   * A slow version of the radius search that iterates through every star in the
   * collection.
   * @param r the radius within which to search.
   * @param pos the Cartesian around which to search.
   * @param starCol the collection of stars to search.
   * @return all stars in starCol within r of pos.
   */
  protected static List<Cartesian> slowRad(
      double r,
      Cartesian pos,
      List<Cartesian> starCol) {
    if (r < 0) {
      System.out.println("ERROR: R must be a nonnegative.");
      return new ArrayList<>();
    }
    MinMaxPriorityQueue<Cartesian> bpq = MinMaxPriorityQueue
        .orderedBy(new CompareDistanceFrom(pos)).create();
    Iterator<Cartesian> iter = starCol.iterator();
    while (iter.hasNext()) {
      Cartesian curr = iter.next();
      if (curr.getDistance(pos) <= r) {
        bpq.add(curr);
      }
    }
    List<Cartesian> toReturn = new ArrayList<Cartesian>();
    while (!bpq.isEmpty()) {
      toReturn.add(bpq.pollFirst());
    }

    return toReturn;
  }
}
