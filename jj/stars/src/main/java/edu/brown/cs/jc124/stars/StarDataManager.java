package edu.brown.cs.jc124.stars;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import edu.brown.cs.jc124.kdtree.KdTree;
import edu.brown.cs.jc124.stars.CommandParser.CommandType;

/**
 * @author jchen
 *
 *         A wrapper class around a star KdTree, used for executing queries
 *         over the tree.
 */
public class StarDataManager {
  // prefer composition, since StarData is a utility wrapper class for executing
  // Main
  private KdTree<Star> starTree;
  private HashMap<String, Star> nameMap;

  /**
   * Creates a database of given stars to query from.
   *
   * @param stars
   *          the list of stars to create a database from
   */
  public StarDataManager(List<Star> stars) {
    starTree = new KdTree<Star>(3, stars);
    nameMap = new HashMap<String, Star>();
    for (Star s : stars) {
      if (!"".equals(s.getName())) {
        nameMap.put(s.getName().toLowerCase(), s);
      }
    }
  }

  private Star getStar(String name) {
    Star point = nameMap.get(name);
    if (point == null) {
      throw new NoSuchElementException("no star with that name");
    }
    return point;
  }

  private List<Star> kNearestNeighbors(String name, int k) {
    Star s = getStar(name);
    // include named star in k
    List<Star> result = starTree.kNearestNeighbors(s, k + 1);
    result.remove(s); // remove named star
    return result;
  }

  private List<Star> radiusSearch(String name, double radius) {
    Star s = getStar(name);
    List<Star> result = starTree.withinDistance(s, radius);
    result.remove(s); // remove named star
    return result;
  }

  private List<Star> kNearestNeighbors(double x, double y, double z, int k) {
    Star point = new Star(-1, "", x, y, z); // dummy star
    return starTree.kNearestNeighbors(point, k);
  }

  private List<Star> radiusSearch(double x, double y, double z, double radius) {
    Star point = new Star(-1, "", x, y, z); // dummy star
    return starTree.withinDistance(point, radius);
  }

  /**
   * Parses and runs a given command that finds stars by x, y, z location.
   *
   * @param sCommand
   *          the string-encoded command parameter
   * @param kr
   *          the string-encoded k or r parameter
   * @param sx
   *          the string-encoded x command parameter
   * @param sy
   *          the string-encoded y command parameter
   * @param sz
   *          the string-encoded z command parameter
   * @return the list of stars that satisfy the query
   */
  public List<Star> locationCommand(String sCommand, String kr, String sx,
      String sy, String sz) {
    // pull command
    CommandType command = CommandParser.parseCommand(sCommand);

    // pull data not specific to command
    double[] xyz = CommandParser.parseXYZ(sx, sy, sz);
    double x = xyz[0];
    double y = xyz[1];
    double z = xyz[2];

    // pull data specific to command, then execute appropriate command
    switch (command) {
      case NEIGHBOR:
        int k = CommandParser.parseK(kr);
        return kNearestNeighbors(x, y, z, k);

      case RADIUS:
        double r = CommandParser.parseR(kr);
        return radiusSearch(x, y, z, r);

      default:
        throw new IllegalArgumentException(
            "command must be 'neighbor' or 'radius'");
    }
  }

  /**
   * Parses and runs a given command that finds stars around a given star (by
   * name).
   *
   * @param sCommand
   *          the string-encoded command parameter
   * @param kr
   *          the string-encoded k or r parameter
   * @param sName
   *          the string-encoded star name parameter
   * @return the list of stars that satisfy the query
   */
  public List<Star> nameCommand(String sCommand, String kr, String sName) {
    // pull command
    CommandType command = CommandParser.parseCommand(sCommand);

    // pull data not specific to command
    String name = CommandParser.parseName(sName);

    // pull data specific to command, then execute appropriate command
    switch (command) {
      case NEIGHBOR:
        int k = CommandParser.parseK(kr);
        return kNearestNeighbors(name, k);

      case RADIUS:
        double r = CommandParser.parseR(kr);
        return radiusSearch(name, r);

      default:
        throw new IllegalArgumentException(
            "command must be 'neighbor' or 'radius'");
    }
  }
}
