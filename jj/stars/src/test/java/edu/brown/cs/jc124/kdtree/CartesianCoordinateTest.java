package edu.brown.cs.jc124.kdtree;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit Tests for CartesianCoordinate.java class.
 */
public class CartesianCoordinateTest {

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
  public void distanceIsPositive() {
    double x1 = Math.random() * 100.0;
    double x2 = -Math.random() * 100.0;
    double y1 = -Math.random() * 100.0;
    double y2 = Math.random() * 100.0;

    CartesianCoordinate c1 = new CartesianCoordinate(2, x1, y1);
    CartesianCoordinate c2 = new CartesianCoordinate(2, x2, y2);

    double distance = c1.distance(c2);
    assertTrue(distance >= 0);
  }

  @Test
  public void samePoint() {
    double x1 = (Math.random() * 200.0) - 100.0;
    double y1 = (Math.random() * 200.0) - 100.0;

    CartesianCoordinate c1 = new CartesianCoordinate(2, x1, y1);

    double distance = c1.distance(c1);
    assertTrue(distance == 0);
  }

  @Test
  public void differentPoints() {
    double x1, y1, x2, y2;
    x1 = (Math.random() * 200.0) - 100.0;
    y1 = (Math.random() * 200.0) - 100.0;

    do {
      x2 = (Math.random() * 200.0) - 100.0;
    } while (x1 == x2);

    do {
      y2 = (Math.random() * 200.0) - 100.0;
    } while (y1 == y2);

    CartesianCoordinate c1 = new CartesianCoordinate(2, x1, y1);
    CartesianCoordinate c2 = new CartesianCoordinate(2, x2, y2);

    double distance = c1.distance(c2);
    assertTrue(distance != 0);
  }

  @Test
  public void oneApart() {
    double x1, y1, x2, y2;
    x1 = (Math.random() * 200.0) - 100.0;
    y1 = (Math.random() * 200.0) - 100.0;

    double radians = Math.random() * 2.0 * Math.PI;
    x2 = x1 + Math.cos(radians);
    y2 = y1 + Math.sin(radians);

    CartesianCoordinate c1 = new CartesianCoordinate(2, x1, y1);
    CartesianCoordinate c2 = new CartesianCoordinate(2, x2, y2);

    double distance = c1.distance(c2);
    assertTrue(distance < 1.001 && distance > 0.999); // floating point
  }
}
