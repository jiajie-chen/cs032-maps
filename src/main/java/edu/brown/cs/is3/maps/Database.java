package edu.brown.cs.is3.maps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.javadocmd.simplelatlng.LatLng;

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

  /**
   * Constructs a db.
   * @param path to the database.
   * @throws ClassNotFoundException if class for name fails.
   * @throws SQLException on database error.
   */
  public Database(String path) throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    this.urlToDB = "jdbc:sqlite:" + path;
    this.conn = DriverManager.getConnection(this.urlToDB);
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
      throw new RuntimeException("ERROR: Failed to generate full way list.");
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

    String nodeQuery = "SELECT latitude, longitude FROM node WHERE id = ? LIMIT 1";
    String wayQuery = "SELECT id, name, end FROM way WHERE start = ?;";

    try (PreparedStatement nodePS = conn.prepareStatement(nodeQuery);
        PreparedStatement wayPS = conn.prepareStatement(wayQuery)) {
      nodePS.setString(1, id);
      wayPS.setString(1, id);

      try (ResultSet nodeRS = nodePS.executeQuery()) {
        Double lat;
        Double lng;

        if (nodeRS.next()) {
          lat = Double.parseDouble(nodeRS.getString(1));
          lng = Double.parseDouble(nodeRS.getString(2));

          if (nodeRS.next()) {
            close();
            throw new RuntimeException("ERROR: Multiple nodes with that id.");
          }
        } else {
          close();
          throw new RuntimeException("ERROR: No node with that id.");
        }

        toReturn = new Node(id, new LatLng(lat, lng));

        try (ResultSet wayRS = wayPS.executeQuery()) {
          while (wayRS.next()) {
            String wayId = wayRS.getString(1);
            String name = wayRS.getString(2);
            String endId = wayRS.getString(3);

            Way w = new Way(wayId, name, id, endId);
            wayById.put(wayId, w);
            toReturn.addWay(w);
          }
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
   * Searches for and returns the way with the given id;
   * @param id to search for.
   * @return the way with that id.
   */
  public Way wayOfId(String id) {
    if (wayById.get(id) != null) {
      return wayById.get(id);
    }

    String wayQuery = "SELECT name, start, end FROM way WHERE id = ? LIMIT 1";

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

          if (wayRS.next()) {
            close();
            throw new RuntimeException("ERROR: Multiple ways with that id.");
          }
        } else {
          close();
          throw new RuntimeException("ERROR: No way with that id.");
        }

        Way toReturn = new Way(id, name, startID, endID);
        wayById.put(id, toReturn);
        return toReturn; // maybe should build nodes!!
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Searches for and returns the way with the given name;
   * @param name to search for.
   * @return the way with that name.
   */
  public Way wayOfName(String name) {
    String wayQuery = "SELECT id, start, end FROM way WHERE name = ? LIMIT 1";

    try (PreparedStatement wayPS = conn.prepareStatement(wayQuery)) {
      wayPS.setString(1, name);

      try (ResultSet wayRS = wayPS.executeQuery()) {
        String id;
        String startID;
        String endID;

        if (wayRS.next()) {
          id = wayRS.getString(1);
          startID = wayRS.getString(2);
          endID = wayRS.getString(3);
        } else {
          close();
          throw new RuntimeException("ERROR: No way with that name.");
        }

        Way toReturn = new Way(id, name, startID, endID);
        wayById.put(id, toReturn);
        return toReturn; // maybe should build nodes!!
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }
}
