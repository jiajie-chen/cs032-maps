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
  private static final double TILE_SIZE = .1;

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
   * Searches for and returns the node with the given id;
   * @param id to search for.
   * @return the node with that id.
   */
  public Node nodeOfId(String id) {
    if (nodeById.get(id) != null) {
      return nodeById.get(id);
    }

    Node toReturn;
    String nodeQuery = "SELECT way.id, way.name, way.end FROM way WHERE way.start = ? "
        + "UNION "
        + "SELECT node.id, node.latitude, node.longitude FROM node WHERE node.id = ? "
        + "ORDER BY id;";

    try (PreparedStatement nodePS = conn.prepareStatement(nodeQuery)) {
      nodePS.setString(1, id);
      nodePS.setString(2, id);

      try (ResultSet nodeRS = nodePS.executeQuery()) {
        Double lat;
        Double lng;

        if (nodeRS.next()) {
          lat = Double.parseDouble(nodeRS.getString(2));
          lng = Double.parseDouble(nodeRS.getString(3));
        } else {
          close();
          throw new RuntimeException("No node with that id.");
        }

        toReturn = new Node(id, new RadianLatLng(lat, lng));

        while (nodeRS.next()) {
          String wayId = nodeRS.getString(1);
          String name = nodeRS.getString(2);
          String endId = nodeRS.getString(3);

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
   * Searches for and returns the way with the given id.
   * @param id to search for.
   * @return the way with that id.
   */
  public Way wayOfId(String id) {
    if (wayById.get(id) != null) {
      return wayById.get(id);
    }

    String wayQuery = "SELECT name, start, end FROM way WHERE id = ? ;"; // LIMIT
                                                                         // 1;";

    try (PreparedStatement wayPS = conn.prepareStatement(wayQuery)) {
      wayPS.setString(1, id);

      try (ResultSet wayRS = wayPS.executeQuery()) {
        String name;
        String startID;
        String endID;

        if (wayRS.next()) {
          name = wayRS.getString(1);
          startID = wayRS.getString(2);
          endID = wayRS.getString(3);
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
   * @return
   */
  public Node nodeOfIntersection(String streetName, String crossName) {
    String interQuery = "SELECT street.start FROM way AS street INNER JOIN way AS cross "
        + "ON street.start = cross.start WHERE "
        + "(street.name = ? AND cross.name = ?) OR (street.name = ? AND cross.name = ?) "
        + "UNION "
        + "SELECT street.start FROM way AS street INNER JOIN way AS cross "
        + "ON street.start = cross.end WHERE "
        + "(street.name = ? AND cross.name = ?) OR (street.name = ? AND cross.name = ?) "
        + "UNION "
        + "SELECT street.end FROM way AS street INNER JOIN way AS cross "
        + "ON street.end = cross.start WHERE "
        + "(street.name = ? AND cross.name = ?) OR (street.name = ? AND cross.name = ?) "
        + "UNION "
        + "SELECT street.end FROM way AS street INNER JOIN way AS cross "
        + "ON street.end = cross.end WHERE "
        + "(street.name = ? AND cross.name = ?) OR (street.name = ? AND cross.name = ?) "
        + ";"; // "LIMIT 1;";

    try (PreparedStatement interPS = conn.prepareStatement(interQuery)) {
      for (int i = 1; i < 17; i += 4) {
        interPS.setString(i, streetName);
        interPS.setString(i + 1, crossName);
        interPS.setString(i + 2, crossName);
        interPS.setString(i + 3, streetName);
      }

      try (ResultSet interRS = interPS.executeQuery()) {
        String nodeID;

        if (interRS.next()) {
          nodeID = interRS.getString(1);
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
          String nodeId = nodeRS.getString(1);
          Double lat = Double.parseDouble(nodeRS.getString(2));
          Double lng = Double.parseDouble(nodeRS.getString(3));

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
   * the northwest corner with a width specified by TILE_SIZE.
   * @param nw
   * @return
   */
  public Tile tileOfCorner(RadianLatLng nw) {
    Tile toReturn;
    double top = nw.getLat();
    double bot = top - TILE_SIZE;
    double left = nw.getLng();
    double right = left + TILE_SIZE;

    if ((toReturn = tileByCorner.get(nw)) != null) {
      return toReturn;
    }

    Set<CompactWay> ways = new HashSet<>();
    String wayQuery = "SELECT start, end FROM way;";
    String nodeQuery = "SELECT latitude, longitude FROM node WHERE (id = ? OR id = ?) "
        + "AND latitude < ? AND latitude > ? AND longitude > ? AND longitude < ?;";

    try (PreparedStatement wayPS = conn.prepareStatement(wayQuery);
        PreparedStatement nodePS = conn.prepareStatement(nodeQuery)) {
      try (ResultSet wayRS = wayPS.executeQuery()) {

        while (wayRS.next()) {
          String startId = wayRS.getString(1);
          String endId = wayRS.getString(2);

          nodePS.setString(1, startId);
          nodePS.setString(2, endId);
          nodePS.setDouble(3, top);
          nodePS.setDouble(4, bot);
          nodePS.setDouble(5, left);
          nodePS.setDouble(6, right);

          try (ResultSet nodeRS = nodePS.executeQuery()) {
            // node order not guaranteed

            boolean first = nodeRS.next();
            RadianLatLng start = new RadianLatLng(
                Double.parseDouble(nodeRS.getString(1)),
                Double.parseDouble(nodeRS.getString(2)));

            boolean second = nodeRS.next();
            RadianLatLng end = new RadianLatLng(
                Double.parseDouble(nodeRS.getString(1)),
                Double.parseDouble(nodeRS.getString(2)));

            if (!(first && second && !nodeRS.next())) {
              throw new RuntimeException("Could not locate nodes in box.");
            }

            ways.add(new CompactWay(start, end));
          }
        }

      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }

    toReturn = new Tile(nw, TILE_SIZE, ways);
    tileByCorner.put(nw, toReturn);
    return toReturn;
  }
}
