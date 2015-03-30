package edu.brown.cs.jc124.stars;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.brown.cs.jc124.stars.CommandParser.CommandType;
import static org.junit.Assert.*;

/**
 * @author jchen
 *
 * Tests for the command parser
 */
public class CommandParserTest {

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
  public void commandTest() {
    String sc = "neighbor";
    CommandType c = CommandParser.parseCommand(sc);
    assertTrue(c == CommandType.NEIGHBOR);
    
    sc = "radius";
    c = CommandParser.parseCommand(sc);
    assertTrue(c == CommandType.RADIUS);
    
    sc = Double.toString(Math.random() * 50);
    try {
      c = CommandParser.parseCommand(sc);
    } catch(IllegalArgumentException e) {
      assertTrue(true);
      return;
    }
    assertTrue(false);
  }
  
  @Test
  public void kTest() {
    int k = (int) (Math.random() * 50.0);
    String sk = Integer.toString(k);
    assertTrue(k == CommandParser.parseK(sk));
  }
  
  @Test
  public void rTest() {
    double r = Math.random() * 50.0;
    String sr = Double.toString(r);
    assertTrue(r == CommandParser.parseR(sr));
  }
  
  @Test
  public void xyzTest() {
    double x = Math.random() * 200.0 - 100.0;
    double y = Math.random() * 200.0 - 100.0;
    double z = Math.random() * 200.0 - 100.0;
    
    String sx = Double.toString(x);
    String sy = Double.toString(y);
    String sz = Double.toString(z);
    
    double[] xyz = CommandParser.parseXYZ(sx, sy, sz);
    assertTrue(x == xyz[0]);
    assertTrue(y == xyz[1]);
    assertTrue(z == xyz[2]);
  }
  
  @Test
  public void nameTest() {
    String sn = Double.toString(Math.random() * 50);
    String snq = "\"" + sn + "\"";
    String n = CommandParser.parseName(snq);
    assertTrue(sn.equals(n));
  }
}
