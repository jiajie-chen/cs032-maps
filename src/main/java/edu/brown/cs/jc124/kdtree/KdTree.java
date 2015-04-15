package edu.brown.cs.jc124.kdtree;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.MinMaxPriorityQueue;

/**
 * @author jchen
 *
 *         An implementation of an immutable KdTree for searching through
 *         spatial/multi-dimensional data.
 *
 * @param <T> the coordinate type this KdTree will operate on
 */
public class KdTree<T extends Coordinate> extends AbstractCollection<T>
    implements RangeSearchable<T> {

  /**
   * @author jchen
   *
   *         The class to hold the tree's node data.
   *
   * @param <T> the coordinate type this KdTree will operate on
   */
  private final class KdNode {
    private KdNode left, right;
    private T coord;
    private int axis;

    private KdNode(T coord, int axis, KdNode left, KdNode right) {
      if (coord.getDimensions() != kDim) {
        throw new DimensionMismatchException(
            "Attempted to insert mismatched dimensions into k-d tree");
      }

      this.coord = coord;
      this.axis = axis;
      this.left = left;
      this.right = right;
    }

    private KdNode(T coord, int axis) {
      this(coord, axis, null, null);
    }
  }

  private int kDim;
  private int size;
  private KdNode root;

  /**
   * Makes a new KdTree that stores coordinates of a certain dimension.
   *
   * @param dimensions the dimension to use in the tree
   * @param coords the coordinates to store
   */
  public KdTree(int dimensions, List<T> coords) {
    kDim = dimensions;
    size = 0;
    root = buildTree(new ArrayList<T>(coords), 0);
  }

  private KdNode buildTree(List<T> coords, int depth) {
    // get current branch axis
    int axis = depth % kDim;

    // base cases
    if (coords.size() == 0) {
      return null;
    } else if (coords.size() == 1) {
      size++;
      return new KdNode(coords.get(0), axis);
    }

    // sort by axis for splitting
    Collections.sort(coords, getSplitComparator(axis));

    int mid = coords.size() / 2;
    T midCoord = coords.get(mid);

    // split coordinates in half by axis
    List<T> leftCoords, rightCoords;
    leftCoords = new ArrayList<T>(coords.subList(0, mid));
    if (mid + 1 > coords.size()) {
      rightCoords = new ArrayList<T>();
    } else {
      rightCoords = new ArrayList<T>(coords.subList(mid + 1, coords.size()));
    }

    // recur over the split lists
    KdNode median = new KdNode(midCoord, axis,
        buildTree(leftCoords, depth + 1), buildTree(rightCoords, depth + 1));

    size++;
    return median;
  }

  private Comparator<T> getSplitComparator(int axis) {
    return new Comparator<T>() {
      @Override
      public int compare(T c1, T c2) {
        return (int) Math.signum(c1.fieldDistance(c2, axis));
      }
    };
  }

  private Comparator<T> getDistComparator(T c) {
    return new Comparator<T>() {
      @Override
      public int compare(T c1, T c2) {
        return (int) Math.signum(c1.squaredDistance(c) - c2.squaredDistance(c));
      }
    };
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Iterator<T> iterator() {
    throw new UnsupportedOperationException("Not used by k-d trees");
  }

  @Override
  public List<T> kNearestNeighbors(T point, int k) {
    if (k <= 0) {
      throw new IllegalArgumentException("k must be a positive integer");
    }
    if (k > size) {
      throw new IndexOutOfBoundsException(
          "k is greater than the total number of coordinates");
    }
    MinMaxPriorityQueue<T> kNearest = MinMaxPriorityQueue
        .orderedBy(getDistComparator(point)).maximumSize(k).create();

    kNearestNeighborsHelper(point, k, root, kNearest);
    List<T> list = new ArrayList<T>(size());
    while (kNearest.size() > 0) {
      list.add(kNearest.removeFirst());
    }
    return list;
  }

  // uses recursion, so needs helper method (accumulator is kNearest)
  private void kNearestNeighborsHelper(T point, int k, KdNode current,
      MinMaxPriorityQueue<T> kNearest) {
    if (current == null) {
      return;
    }

    kNearest.add(current.coord);

    // recur using binary tree search over axis
    double cmp = point.fieldDistance(current.coord, current.axis);
    if (cmp < 0) {
      kNearestNeighborsHelper(point, k, current.left, kNearest);
    } else {
      kNearestNeighborsHelper(point, k, current.right, kNearest);
    }

    // check other subtree if it is a candidate (queue not filled or search
    // radius in bounds of other subtree)
    double sqDist = kNearest.peekLast().squaredDistance(point);
    if (kNearest.size() < k || Math.pow(cmp, 2) < sqDist) {
      if (cmp < 0) {
        kNearestNeighborsHelper(point, k, current.right, kNearest);
      } else {
        kNearestNeighborsHelper(point, k, current.left, kNearest);
      }
    }
  }

  @Override
  public List<T> withinDistance(T point, double distance) {
    if (distance < 0) {
      throw new IllegalArgumentException("distance must be a positive");
    }

    MinMaxPriorityQueue<T> within = MinMaxPriorityQueue.orderedBy(
        getDistComparator(point)).create();

    withinDistanceHelper(point, Math.pow(distance, 2), root, within);
    List<T> list = new ArrayList<T>(size());
    while (within.size() > 0) {
      list.add(within.removeFirst());
    }
    return list;
  }

  // uses recursion, so needs helper method (accumulator is within)
  private void withinDistanceHelper(T point, double sqDist, KdNode current,
      MinMaxPriorityQueue<T> within) {
    if (current == null) {
      return;
    }

    // add current if in range
    if (point.squaredDistance(current.coord) <= sqDist) {
      within.add(current.coord);
    }

    // recur using binary tree search over axis
    double cmp = point.fieldDistance(current.coord, current.axis);
    if (cmp < 0) {
      withinDistanceHelper(point, sqDist, current.left, within);
    } else {
      withinDistanceHelper(point, sqDist, current.right, within);
    }

    // check other subtree if it is a candidate (search radius in bounds of
    // other subtree)
    if (Math.pow(cmp, 2) <= sqDist) {
      if (cmp < 0) {
        withinDistanceHelper(point, sqDist, current.right, within);
      } else {
        withinDistanceHelper(point, sqDist, current.left, within);
      }
    }
  }
}
