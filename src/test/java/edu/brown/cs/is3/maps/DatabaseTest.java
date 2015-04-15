package edu.brown.cs.is3.maps;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.brown.cs.is3.cartesian.RadianLatLng;
import edu.brown.cs.is3.cartesian.Tile;

import org.junit.Test;

public class DatabaseTest {

  @Test
  public void buildTest() throws ClassNotFoundException, SQLException {
    Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");
    db.close();
    assertTrue(true);
  }

  @Test
  public void badFileTest() throws ClassNotFoundException {
    try {
      Database db = new Database("foo.bar");
      fail("Invalid file.");
    } catch (SQLException e) {
      assertTrue(true);
    }
  }

  @Test
  public void badTypeFileTest() throws ClassNotFoundException {
    try {
      Database db = new Database("pom.xml");
      fail("Invalid file.");
    } catch (SQLException e) {
      assertTrue(true);
    }
  }

  @Test
  public void directoryTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/");
      fail("Invalid path.");
    } catch (SQLException e) {
      assertTrue(true);
    }
  }

  @Test
  public void wayNamesTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");

      Set<String> names = db.allWayNames();
      Set<String> known = Sets.newHashSet("Chihiro Ave", "Radish Spirit Blvd",
          "Sootball Ln", "Kamaji Pl", "Yubaba St");

      assertTrue(names.equals(known));
    } catch (SQLException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void nodeOfIdTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");

      Node one = new Node("/n/1", 41.8203, -71.4);
      Node n = db.nodeOfId("/n/1");

      assertTrue(one.equals(n) && one.getPos().equals(n.getPos()));
      assertTrue(n.getWayIDs().equals(Sets.newHashSet("/w/3", "/w/1")));

      Node m = db.nodeOfId("/n/5");
      assertTrue(m.getWayIDs().equals(new HashSet<>()));

      db.close();

      assertTrue(n.equals(db.nodeOfId("/n/1"))); // testing caching
    } catch (SQLException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void noSuchNodeTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");

      try {
        Node n = db.nodeOfId("foo");
        fail("Found non-existant node.");
      } catch (RuntimeException e) {
        assertTrue(true);
      }

    } catch (SQLException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void wayOfIdTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");

      Way one = new Way("/w/1", "Chihiro Ave", "/n/1", "/n/2");
      Way w = db.wayOfId("/w/1");

      assertTrue(one.equals(w));
      assertTrue(w.getStartId().equals(w.getStartId()) &&
          w.getEndId().equals(one.getEndId()));

      db.close();

      assertTrue(w.equals(db.wayOfId("/w/1"))); // testing caching
    } catch (SQLException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void noSuchWayTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");

      try {
        Way n = db.wayOfId("foo");
        fail("Found non-existant way.");
      } catch (RuntimeException e) {
        assertTrue(true);
      }

    } catch (SQLException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void nodeOfIntersectionTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");

      String rad = "Radish Spirit Blvd";
      String yub = "Yubaba St";
      String chi = "Chihiro Ave";
      String kam = "Kamaji Pl";
      String sot = "Sootball Ln";

      Node n = db.nodeOfIntersection(rad, chi);
      Node m = db.nodeOfIntersection(chi, rad);

      assertTrue(n.equals(m));
      assertTrue(n.equals(db.nodeOfId("/n/0")));

      n = db.nodeOfIntersection(chi, sot);
      assertTrue(n.equals(db.nodeOfId("/n/1")));

      n = db.nodeOfIntersection(kam, chi);
      assertTrue(n.equals(db.nodeOfId("/n/2")));

      n = db.nodeOfIntersection(yub, yub);
      assertTrue(n.equals(db.nodeOfId("/n/3")));

      n = db.nodeOfIntersection(kam, yub);
      assertTrue(n.equals(db.nodeOfId("/n/5")));
    } catch (SQLException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void noIntersectionTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");

      String rad = "Radish Spirit Blvd";
      String yub = "Yubaba St";
      String chi = "Chihiro Ave";
      String kam = "Kamaji Pl";
      String sot = "Sootball Ln";

      try {
        Node n = db.nodeOfIntersection(chi, yub);
        fail("Found a fake intersection.");
      } catch (RuntimeException e) {
        assertTrue(true);
      }

      try {
        Node n = db.nodeOfIntersection(rad, kam);
        fail("Found a fake intersection.");
      } catch (RuntimeException e) {
        assertTrue(true);
      }

      try {
        Node n = db.nodeOfIntersection("foo", yub);
        fail("Found a fake intersection.");
      } catch (RuntimeException e) {
        assertTrue(true);
      }

      try {
        Node n = db.nodeOfIntersection("foo", "bar");
        fail("Found a fake intersection.");
      } catch (RuntimeException e) {
        assertTrue(true);
      }
    } catch (SQLException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void allKdMapNodeTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");

      Set<KdMapNode> all = db.allKdMapNodes();

      Set<KdMapNode> a = new HashSet<>();
      for (int i = 0; i < 6; i++) {
        Node n = db.nodeOfId("/n/" + i);
        a.add(new KdMapNode(n.getId(), n.getPos().getLat(), n.getPos().getLng()));
      }

      assertTrue(a.equals(all));
    } catch (SQLException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void tileOfCornerTest() throws ClassNotFoundException {
    try {
      Database db = new Database("/course/cs032/data/maps/smallMaps.sqlite3");

      Tile empty = db.tileOfCorner(new RadianLatLng(), 1.0);
      Tile less = db.tileOfCorner(new RadianLatLng(1, 1), 0.0);
      Tile two = db.tileOfCorner(new RadianLatLng(41.82, -71.4), 0);
      Tile six = db.tileOfCorner(new RadianLatLng(41.8203, -71.4003), .0003);
      Tile all = db.tileOfCorner(new RadianLatLng(41.8206, -71.4003), .01);

      RadianLatLng[] cords = new RadianLatLng[6];
      for (int i = 0; i < 6; i++) {
        Node n = db.nodeOfId("/n/" + i);
        cords[i] = n.getPos();
      }

      Set<CompactWay> s = new HashSet<>();
      assertTrue(empty.equals(new Tile(new RadianLatLng(), 1, s)));
      assertTrue(less.equals(new Tile(new RadianLatLng(1, 1), 0, s)));

      s.add(new CompactWay(cords[0], cords[1]));
      s.add(new CompactWay(cords[0], cords[3]));
      assertTrue(two.equals(new Tile(cords[0], 0, s)));

      s.add(new CompactWay(cords[1], cords[4]));
      s.add(new CompactWay(cords[3], cords[4]));
      s.add(new CompactWay(cords[1], cords[2]));
      s.add(new CompactWay(cords[4], cords[5]));
      assertTrue(six.equals(new Tile(cords[4], .0003, s)));

      s.add(new CompactWay(cords[2], cords[5]));
      assertTrue(all.equals(
          new Tile(new RadianLatLng(41.8206, -71.4003), .01, s)));

      Tile ends = db.tileOfCorner(cords[2], 0);
      Set<CompactWay> st = new HashSet<>();
      st.add(new CompactWay(cords[1], cords[2]));
      st.add(new CompactWay(cords[2], cords[5]));
      assertTrue(ends.equals(new Tile(cords[2], 0, st)));
    } catch (SQLException e) {
      fail(e.getMessage());
    }
  }
}
