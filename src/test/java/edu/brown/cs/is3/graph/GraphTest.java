package edu.brown.cs.is3.graph;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import edu.brown.cs.is3.maps.Database;
import edu.brown.cs.is3.maps.Node;
import edu.brown.cs.is3.maps.Way;

import org.junit.Test;

public class GraphTest {

  @Test
  public void singleTest() throws ClassNotFoundException, SQLException {
    Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");
    Graph g = new Graph(db);

    Node start = db.nodeOfId("/n/0");
    Node end = db.nodeOfId("/n/5");

    List<Way> path = g.dijkstras(start, end).getPath();
    assertTrue(path.equals(Arrays.asList(db.wayOfId("/w/0"),
        db.wayOfId("/w/1"), db.wayOfId("/w/4"))));
  }

  @Test
  public void doubleTest() throws ClassNotFoundException, SQLException {
    Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");
    Graph g = new Graph(db);

    Node start = db.nodeOfId("/n/0");
    Node end = db.nodeOfId("/n/5");

    List<Way> path = g.dijkstras(start, end).getPath();
    assertTrue(path.equals(Arrays.asList(db.wayOfId("/w/0"),
        db.wayOfId("/w/1"), db.wayOfId("/w/4"))));

    start = db.nodeOfId("/n/3");

    path = g.dijkstras(start, end).getPath();
    assertTrue(path
        .equals(Arrays.asList(db.wayOfId("/w/5"), db.wayOfId("/w/6"))));
  }

  @Test
  public void noPathTest() throws ClassNotFoundException, SQLException {
    Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");
    Graph g = new Graph(db);

    Node start = db.nodeOfId("/n/5");
    Node end = db.nodeOfId("/n/0");

    List<Way> path = g.dijkstras(start, end).getPath();
    assertTrue(path == null);
  }

  @Test
  public void sameNodeTest() throws ClassNotFoundException, SQLException {
    Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");
    Graph g = new Graph(db);

    Node start = db.nodeOfId("/n/0");
    Node end = db.nodeOfId("/n/0");

    try {
      @SuppressWarnings("unused")
      List<Way> path = g.dijkstras(start, end).getPath();
      assertTrue(false);
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }
}
