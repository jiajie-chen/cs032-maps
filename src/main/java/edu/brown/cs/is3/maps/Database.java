package edu.brown.cs.is3.maps;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.brown.cs.is3.cartesian.RadianLatLng;
import edu.brown.cs.is3.cartesian.Tile;

/**
 * Class for interacting with the maps database and building objects from data.
 * @author is3
 *
 */
public class Database {
  private final String urlToDB;
  private final Connection conn;
  private final Map<String, Way> wayById = new HashMap<>();
  private final Map<String, Node> nodeById = new HashMap<>();
  private final Map<RadianLatLng, Tile> tileByCorner = new HashMap<>();

  private static final int FIRST = 1;
  private static final int SECOND = 2;
  private static final int THIRD = 3;
  private static final int FOURTH = 4;

  /**
   * Constructs a db.
   * @param path to the database.
   * @throws ClassNotFoundException if class for name fails.
   * @throws SQLException on database error.
   */
  public Database(String path) throws ClassNotFoundException, SQLException {
    File f = new File(path);
    if (!f.exists() || !f.isFile() || !path.endsWith(".sqlite3")) {
      throw new SQLException("Invalid database file: " + path + ".");
    }

    Class.forName("org.sqlite.JDBC");
    this.urlToDB = "jdbc:sqlite:" + path;
    this.conn = DriverManager.getConnection(this.urlToDB);
    if (conn == null || conn.isClosed()) {
      throw new SQLException("Failed to create a database at path: " + path);
    }
  }

  /**
   * Frees all resources associated with database.
   */
  public void close() {
    try {
      conn.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Generates a list of all the way names in the database.
   * @return list of way names.
   */
  public Set<String> allWayNames() {
    Set<String> toReturn = new HashSet<>();

    String query = "SELECT DISTINCT name FROM way;";

    try (PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        toReturn.add(rs.getString(1));
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException("Failed to generate full way list.");
    }

    return toReturn;
  }

  /**
   * Searches for and returns the node with the given id including its ways.
   * @param id to search for.
   * @return the node with that id.
   */
  public Node slowNodeOfId(String id) {
    if (nodeById.get(id) != null) {
      return nodeById.get(id);
    }

    Node toReturn;
    String nodeQuery =
        "SELECT way.id, way.name, way.end FROM way WHERE way.start = ? "
            + "UNION SELECT node.id, node.latitude, node.longitude "
            + "FROM node WHERE node.id = ? "
            + "ORDER BY id;";

    try (PreparedStatement nodePS = conn.prepareStatement(nodeQuery)) {
      nodePS.setString(FIRST, id);
      nodePS.setString(SECOND, id);

      try (ResultSet nodeRS = nodePS.executeQuery()) {
        Double lat;
        Double lng;

        if (nodeRS.next()) {
          lat = Double.parseDouble(nodeRS.getString(SECOND));
          lng = Double.parseDouble(nodeRS.getString(THIRD));
        } else {
          close();
          throw new RuntimeException("No node with that id.");
        }

        toReturn = new Node(id, new RadianLatLng(lat, lng));

        while (nodeRS.next()) {
          String wayId = nodeRS.getString(FIRST);
          String name = nodeRS.getString(SECOND);
          String endId = nodeRS.getString(THIRD);

          Way w = new Way(wayId, name, id, endId);
          wayById.put(wayId, w);
          toReturn.addWay(w);
        }

        nodeById.put(id, toReturn);
        return toReturn;
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the node with the given id without pulling ways.
   * @param id of node to examine.
   * @return the node with the given id.
   */
  public Node nodeOfId(String id) {
    if (nodeById.get(id) != null) {
      return nodeById.get(id);
    }

    Node toReturn;
    String nodeQuery = "SELECT latitude, longitude FROM node WHERE id = ?;";

    try (PreparedStatement nodePS = conn.prepareStatement(nodeQuery)) {
      nodePS.setString(FIRST, id);

      try (ResultSet nodeRS = nodePS.executeQuery()) {
        Double lat;
        Double lng;

        if (nodeRS.next()) {
          lat = Double.parseDouble(nodeRS.getString(FIRST));
          lng = Double.parseDouble(nodeRS.getString(SECOND));
        } else {
          close();
          throw new RuntimeException("No node with that id.");
        }

        toReturn = new Node(id, new RadianLatLng(lat, lng));
        nodeById.put(id, toReturn);
        return toReturn;
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Produces all of the ways emanating from a given node and fills in that
   * node.
   * @param n node to examine.
   * @return the set of ways starting at n.
   */
  public Set<Way> waysOfNode(Node n) {
    if (!n.getWays().isEmpty()) {
      return n.getWays();
    }

    String wayQuery = "SELECT id, name, end FROM way WHERE start = ?;";

    try (PreparedStatement wayPS = conn.prepareStatement(wayQuery)) {
      wayPS.setString(1, n.getId());

      try (ResultSet wayRS = wayPS.executeQuery()) {
        String wayId;
        String name;
        String endID;

        while (wayRS.next()) {
          wayId = wayRS.getString(FIRST);
          name = wayRS.getString(SECOND);
          endID = wayRS.getString(THIRD);

          Way toAdd = new Way(wayId, name, n.getId(), endID);
          wayById.put(wayId, toAdd);
          n.addWay(toAdd);
        }

        return n.getWays();
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Searches for and returns the way with the given id.
   * @param id to search for.
   * @return the way with that id.
   */
  public Way wayOfId(String id) {
    if (wayById.get(id) != null) {
      return wayById.get(id);
    }

    String wayQuery = "SELECT name, start, end FROM way WHERE id = ? ;";

    try (PreparedStatement wayPS = conn.prepareStatement(wayQuery)) {
      wayPS.setString(1, id);

      try (ResultSet wayRS = wayPS.executeQuery()) {
        String name;
        String startID;
        String endID;

        if (wayRS.next()) {
          name = wayRS.getString(FIRST);
          startID = wayRS.getString(SECOND);
          endID = wayRS.getString(THIRD);
        } else {
          close();
          throw new RuntimeException("No way with that id.");
        }

        Way toReturn = new Way(id, name, startID, endID);
        wayById.put(id, toReturn);
        return toReturn;
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Finds an intersection between two streets with a given name. If multiple
   * such intersections exist, it returns one such intersection. Does not cache.
   * @param streetName first street.
   * @param crossName second street.
   * @return a node at an intersection of the two named streets.
   */
  public Node nodeOfIntersection(String streetName, String crossName) {
    String interQuery = "SELECT street.start FROM way AS street "
        + "INNER JOIN way AS cross ON street.start = cross.start WHERE "
        + "(street.name = ? AND cross.name = ?) "
        + "UNION "
        + "SELECT street.start FROM way AS street INNER JOIN way AS cross "
        + "ON street.start = cross.end WHERE "
        + "(street.name = ? AND cross.name = ?) "
        + "UNION "
        + "SELECT street.end FROM way AS street INNER JOIN way AS cross "
        + "ON street.end = cross.start WHERE "
        + "(street.name = ? AND cross.name = ?) "
        + "UNION "
        + "SELECT street.end FROM way AS street INNER JOIN way AS cross "
        + "ON street.end = cross.end WHERE "
        + "(street.name = ? AND cross.name = ?) "
        + "LIMIT 1;";

    try (PreparedStatement interPS = conn.prepareStatement(interQuery)) {
      for (int i = 0; i < (FOURTH * SECOND); i += 2) {
        interPS.setString(i + 1, streetName);
        interPS.setString(i + 2, crossName);
      }

      try (ResultSet interRS = interPS.executeQuery()) {
        String nodeID;

        if (interRS.next()) {
          nodeID = interRS.getString(FIRST);
        } else {
          close();
          throw new RuntimeException("No intersection between " + streetName
              + " and " + crossName + ".");
        }

        return nodeOfId(nodeID);
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves but does not cache all node objects from the database.
   * @return the set of all unique node objects in the database.
   */
  public Set<KdMapNode> allKdMapNodes() {
    Set<KdMapNode> toReturn = new HashSet<>();
    String nodeQuery = "SELECT id, latitude, longitude FROM node;";

    try (PreparedStatement nodePS = conn.prepareStatement(nodeQuery)) {
      try (ResultSet nodeRS = nodePS.executeQuery()) {

        while (nodeRS.next()) {
          String nodeId = nodeRS.getString(FIRST);
          Double lat = Double.parseDouble(nodeRS.getString(SECOND));
          Double lng = Double.parseDouble(nodeRS.getString(THIRD));

          KdMapNode n = new KdMapNode(nodeId, lat, lng);
          toReturn.add(n);
        }
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }

    return toReturn;
  }

  /**
   * Searches for and returns a tile containing a set of compact ways based on
   * the northwest corner with a width specified by TILE_SIZE. Assumes there can
   * only be one tile beginning at a given point.
   * @param nw corner of tile to look for.
   * @param width of the bounding box.
   * @return a tile containing a square bounding box of size width extending
   *         south and east from nw.
   */
  public Tile tileOfCorner(RadianLatLng nw, double width) {
    Tile toReturn = tileByCorner.get(nw);

    if (toReturn != null) {
      return toReturn;
    }

    double top = nw.getLat();
    double bot = top - width;
    double left = nw.getLng();
    double right = left + width;

    Set<CompactWay> ways = new HashSet<>();

    String wayQuery = "SELECT w.id, s.latitude, s.longitude, w.end "
        + "FROM way AS w INNER JOIN node AS s ON w.start = s.id "
        + "WHERE  (s.latitude <= ? AND s.latitude >= ? "
        + "AND s.longitude >= ? AND s.longitude <= ?) "
        + "UNION SELECT w.id, e.latitude, e.longitude, w.start "
        + "FROM way AS w INNER JOIN node AS e ON w.end = e.id "
        + "WHERE (e.latitude <= ? AND e.latitude >= ? "
        + "AND e.longitude >= ? AND e.longitude <= ?);";
    try (PreparedStatement wayPS = conn.prepareStatement(wayQuery)) {
      for (int i = 0; i < FOURTH * SECOND; i += FOURTH) {
        wayPS.setDouble(i + FIRST, top);
        wayPS.setDouble(i + SECOND, bot);
        wayPS.setDouble(i + THIRD, left);
        wayPS.setDouble(i + FOURTH, right);
      }

      try (ResultSet wayRS = wayPS.executeQuery()) {
        while (wayRS.next()) {
          RadianLatLng start = new RadianLatLng(
              Double.parseDouble(wayRS.getString(SECOND)),
              Double.parseDouble(wayRS.getString(THIRD)));

          RadianLatLng end = coordinatesOfId(wayRS.getString(FOURTH));

          ways.add(new CompactWay(start, end, wayRS.getString(FIRST)));
        }
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }

    toReturn = new Tile(nw, width, ways);
    tileByCorner.put(nw, toReturn);
    return toReturn;
  }

  /**
   * Generates the coordinate object for a node given its id.
   * @param id of node to look for.
   * @return position object for that node.
   */
  public RadianLatLng coordinatesOfId(String id) {
    if (nodeById.get(id) != null) {
      return nodeById.get(id).getPos();
    }

    String nodeQuery =
        "SELECT node.latitude, node.longitude FROM node WHERE node.id = ?;";

    try (PreparedStatement nodePS = conn.prepareStatement(nodeQuery)) {
      nodePS.setString(FIRST, id);

      try (ResultSet nodeRS = nodePS.executeQuery()) {
        Double lat;
        Double lng;

        if (nodeRS.next()) {
          lat = Double.parseDouble(nodeRS.getString(FIRST));
          lng = Double.parseDouble(nodeRS.getString(SECOND));
        } else {
          close();
          throw new RuntimeException("No node with that id.");
        }

        return new RadianLatLng(lat, lng);
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }
}
