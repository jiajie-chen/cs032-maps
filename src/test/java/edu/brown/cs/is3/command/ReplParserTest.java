package edu.brown.cs.is3.command;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ReplParserTest {

  @Test
  public void baseCasesTest() {
    Command c;

    try {
      c = new ReplParser(" ").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser(" asdf \"dasff ").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("null").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("\"1\"").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("\"1\" \"1\" \"1\" \"1\" \"1\"").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("\"1\" \"1\" 1 \"1\" \"1\"").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("\"1\"\" \"1\" 1 \"1\"").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("1 2 3 4 5").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("1 2 3").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("1 2 3 4fasadf").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("NaN 2 3 4").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("200 2 3 4").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("1 200 3 4").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("      2 3 4 \\asdfd'!@$!#$").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser(
          "das f dfsad fas fd asf das f asfd fas dasf fas fd asf asd fdsa"
              + " fa das "
              + "f dfsad fas fd asf das f asfd fas dasf fas fd asf asd fdsa fa"
              + " das f df"
              + "sad fas fd asf das f asfd fas dasf fas fd asf asd fdsa fadas"
              + " f dfsad fas"
              + " fd asf das f asfd fas dasf fas fd asf asd fdsa fadas f dfsa"
              + "d fas fd asf"
              + " das f asfd fas dasf fas fd asf asd fdsa fadas f dfsad fas f"
              + "d asf das f asf"
              + "d fas dasf fas fd asf asd fdsa fadas f dfsad fas fd asf das"
              + " f asfd fas dasf"
              + " fas fd asf asd fdsa fadas f dfsad fas fd asf das f asfd fa"
              + "s dasf fas fd asf asd fdsa fadas f dfsad fas fd asf das f a"
              + "sfd fas dasf fas fd asf asd fdsa fadas f dfsad fas fd asf d"
              + "as f asfd fas dasf fas fd asf asd fdsa fadas f dfsad fas fd"
              + " asf das f asfd fas dasf fas fd asf asd fdsa fadas f dfsad fa"
              + "s fd asf das f asfd fas dasf fas fd asf asd fdsa fadas f dfs"
              + "ad fas fd asf das f asfd fas dasf fas fd asf asd fdsa fadas "
              + "f dfsad fas fd asf das f asfd fas dasf fas fd asf asd fdsa f"
              + "adas f dfsad fas fd asf das f asfd fas dasf fas fd asf asd f"
              + "dsa fadas f dfsad fas fd asf das f asfd fas dasf fas fd asf "
              + "asd fdsa fadas f dfsad fas fd asf das f asfd fas dasf fas fd"
              + " asf asd fdsa fadas f dfsad fas fd asf das f asfd fas dasf f"
              + "as fd asf asd fdsa fadas f dfsad fas fd asf das f asfd fas da"
              + "sf fas fd asf asd fdsa fadas f dfsad fas fd asf das f asfd f"
              + "as dasf fas fd asf asd fdsa fadas f dfsad fas fd asf das f as"
              + "fd fas dasf fas fd asf asd fdsa fa"
              + " sdf dsfa dfsa df dsfa fas fdas f asf dasf fasd fd asf "
              + "sad fdsa "
              + "f dsaf dasf dasf dfsa f").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser(
          "\"1\" \"1\" \"1\" \"1\" \"1\" \"1\" \"1\" \"1\" \"1\""
              + " \"1\" \"1\" \"1\" \"1\" \"1\" \"1\"").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("\"1\" \"@$%$@%$!!!  \" \"1\" \"1\"").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("\"1\" \"@$%$@%$!!!  \" \"1\" \"1\"").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("\"b\" \"a\" \"c\" \"1").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      c = new ReplParser("\"b\"\"a\" \"c\" \"d\"").parse();
      fail("Allowed bad argument");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }

  @Test
  public void streetNamesTest() {
    Command c;

    try {
      c = new ReplParser("\"A\" \"A\" \"A\" \"A\"").parse();
      assertTrue(c instanceof StreetCommand);
    } catch (IllegalArgumentException e) {
      fail("Didn't parse valid command.");
    }

    try {
      c = new ReplParser("\"A asdfdasf sdaf\" \"A\" \"A\" \"A\"").parse();
      assertTrue(c instanceof StreetCommand);
    } catch (IllegalArgumentException e) {
      fail("Didn't parse valid command.");
    }

    try {
      c = new ReplParser("   \"A\"        \"A\" \"A\" \"A\"  ").parse();
      assertTrue(c instanceof StreetCommand);
    } catch (IllegalArgumentException e) {
      fail("Didn't parse valid command.");
    }

    try {
      c = new ReplParser("\"1A\" \"-A\" \".A\" \"A\"").parse();
      assertTrue(c instanceof StreetCommand);
    } catch (IllegalArgumentException e) {
      fail("Didn't parse valid command.");
    }

    try {
      c = new ReplParser("\"1\" \"1\" \"1\" \"1\"").parse();
      assertTrue(c instanceof StreetCommand);
    } catch (IllegalArgumentException e) {
      fail("Didn't parse valid command.");
    }
  }

  @Test
  public void pointsTest() {
    Command c;

    // try {
    // c = new ReplParser("\"1\" 1 \"1\" \"1\"").parse();
    // assertTrue(c instanceof LatLngCommand);
    // } catch (IllegalArgumentException e) {
    // fail("Didn't parse valid command.");
    // }

    // try {
    // c = new ReplParser("\"-1\" 1 \"1\" \"1\"").parse();
    // assertTrue(c instanceof LatLngCommand);
    // } catch (IllegalArgumentException e) {
    // fail("Didn't parse valid command.");
    // }

    try {
      c = new ReplParser("1 2 3 4").parse();
      assertTrue(c instanceof LatLngCommand);
    } catch (IllegalArgumentException e) {
      fail("Didn't parse valid command.");
    }

    try {
      c = new ReplParser("-1 2 3 -4").parse();
      assertTrue(c instanceof LatLngCommand);
    } catch (IllegalArgumentException e) {
      fail("Didn't parse valid command.");
    }

    try {
      c = new ReplParser("-1 2 3 -4").parse();
      assertTrue(c instanceof LatLngCommand);
    } catch (IllegalArgumentException e) {
      fail("Didn't parse valid command.");
    }

    try {
      c = new ReplParser("-1.588863 2.087491 3 -4").parse();
      assertTrue(c instanceof LatLngCommand);
    } catch (IllegalArgumentException e) {
      fail("Didn't parse valid command.");
    }

    try {
      c = new ReplParser("  -1.588863     2.087491 3      -4  ").parse();
      assertTrue(c instanceof LatLngCommand);
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      fail("Didn't parse valid command.");
    }
  }
}
