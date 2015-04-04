package edu.brown.cs.is3.maps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for interacting with the maps database and building objects from data.
 * @author is3
 *
 */
public class Database {
  private final String urlToDB;
  private final Connection conn;

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

  public Node nodeOfId(String id) {
    String nodeQuery = "SELECT latitude, longitude FROM node WHERE id = ? LIMIT 1";

    try (PreparedStatement nodePS = conn.prepareStatement(nodeQuery)) {
      nodePS.setString(1, id);

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

        return new Node(new LatLng(id, lat, lng));
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  public Way wayOfId(String id) {
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

        return new Way(id, name, startID, endID); // maybe should build nodes!!
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

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

        return new Way(id, name, startID, endID); // maybe should build nodes!!
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }
}
