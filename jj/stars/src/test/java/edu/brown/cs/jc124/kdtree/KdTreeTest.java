package edu.brown.cs.jc124.kdtree;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author jchen
 * 
 *         Tests for KdTree
 */
public class KdTreeTest {

  @BeforeClass
  public static void setUpClass() throws Exception {
    // (Optional) Code to run before any tests begin goes here.
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    // (Optional) Code to run after all tests finish goes here.
  }

  @Before
  public void setUp() {
    // (Optional) Code to run before each test case goes here.
  }

  @After
  public void tearDown() {
    // (Optional) Code to run after each test case goes here.
  }

  // TODO add test methods here.
  // The methods must be annotated with annotation @Test. For example:
  //
  // @Test
  // public void hello() {}

  @Test
  public void kNNTest() {
    double oX, oY, x, y;

    // make origin point
    oX = (Math.random() * 200.0) - 100.0;
    oY = (Math.random() * 200.0) - 100.0;
    CartesianCoordinate o = new CartesianCoordinate(2, oX, oY);

    int k = (int) (Math.random() * 50);
    List<CartesianCoordinate> init = new ArrayList<CartesianCoordinate>();
    List<CartesianCoordinate> comp = new ArrayList<CartesianCoordinate>();
    // random points close to origin
    for (int i = 0; i < k; i++) {
      double radians = Math.random() * 2.0 * Math.PI;
      double radius = Math.random() * 50.0;
      x = oX + Math.cos(radians) * radius;
      y = oY + Math.sin(radians) * radius;

      CartesianCoordinate c = new CartesianCoordinate(2, x, y);
      init.add(c);
      comp.add(c);
    }
    // random points far from origin
    int k2 = (int) (Math.random() * 50);
    for (int i = 0; i < k2; i++) {
      double radians = Math.random() * 2.0 * Math.PI;
      double radius = Math.random() * 50.0 + 50.0;
      x = oX + Math.cos(radians) * radius;
      y = oY + Math.sin(radians) * radius;

      CartesianCoordinate c = new CartesianCoordinate(2, x, y);
      init.add(c);
    }

    List<CartesianCoordinate> kd = new KdTree<CartesianCoordinate>(2, init)
        .kNearestNeighbors(o, k);
    assertTrue(comp.size() == kd.size());
    assertTrue(comp.containsAll(kd));
  }

  @Test
  public void rangeTest() {
    double oX, oY, x, y, distance;

    distance = Math.random() * 200.0;

    // make origin point
    oX = (Math.random() * 200.0) - 100.0;
    oY = (Math.random() * 200.0) - 100.0;
    CartesianCoordinate o = new CartesianCoordinate(2, oX, oY);

    int k = (int) (Math.random() * 50);
    List<CartesianCoordinate> init = new ArrayList<CartesianCoordinate>();
    List<CartesianCoordinate> comp = new ArrayList<CartesianCoordinate>();
    // random points close to origin
    for (int i = 0; i < k; i++) {
      double radians = Math.random() * 2.0 * Math.PI;
      double radius = Math.random() * distance;
      x = oX + Math.cos(radians) * radius;
      y = oY + Math.sin(radians) * radius;

      CartesianCoordinate c = new CartesianCoordinate(2, x, y);
      init.add(c);
      comp.add(c);
    }
    // random points far from origin
    int k2 = (int) (Math.random() * 50);
    for (int i = 0; i < k2; i++) {
      double radians = Math.random() * 2.0 * Math.PI;
      double radius = Math.random() * 50.0 + distance;
      x = oX + Math.cos(radians) * radius;
      y = oY + Math.sin(radians) * radius;

      CartesianCoordinate c = new CartesianCoordinate(2, x, y);
      init.add(c);
    }

    List<CartesianCoordinate> kd = new KdTree<CartesianCoordinate>(2, init)
        .withinDistance(o, distance);
    assertTrue(comp.size() == kd.size());
    assertTrue(comp.containsAll(kd));
  }
}
