package edu.brown.cs.is3.stars;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.is3.cartesian.Cartesian;
import edu.brown.cs.is3.cartesian.Position;
import edu.brown.cs.is3.kdtree.KDTree;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Main class for Stars. Parses the input file, handles options, and allows
 * the user to make various queries on the input data using a kd tree through
 * either the terminal or a gui.
 * Run queries using ./run filename [--gui].
 * @author is3
 */
public final class Main {
  private static final String[] EXPECTED_FIELDS =
  {"StarID", "ProperName", "X", "Y", "Z"};
  public static final int ID_FIELD = 0;
  public static final int NAME_FIELD = 1;
  public static final int X_FIELD = 2;
  public static final int Y_FIELD = 3;
  public static final int Z_FIELD = 4;
  private static final int LONG_COMMAND_LENGTH = 5;
  private static final int SHORT_COMMAND_LENGTH = 3;
  private static final int PORT_NUMBER = 4567;
  private static KDTree<Star> starTree;
  private static HashMap<String, Star> starMap;
  private static Collection<Cartesian> starCol;

  private Main() {
    return;
  }

  /**
   * Main method. Handles the input arguments with jopt and either
   * launches the gui or handles terminal based requests.
   * @param args input arguments in the format ./run filename [--gui]
   * @throws IOException on file not found or read errors
   */
  public static void main(String[] args) throws IOException {
    OptionParser parser = new OptionParser();

    parser.accepts("gui");
    OptionSpec<File> fileSpec = parser.nonOptions().ofType(File.class);
    OptionSet options = parser.parse(args);
    File db;

    try {
      db = options.valueOf(fileSpec);
    } catch (Exception e) {
      System.out.println("ERROR: Invalid options.");
      return;
    }
    if (db == null) {
      System.out.println("ERROR: Please specify a star file.");
      return;
    } else {
      try {
        starMap = parseFile(db);
      } catch (IllegalArgumentException e) {
        return;
      } catch (IOException e) {
        return;
      }
    }
    starTree = new KDTree<Star>(starCol);

    if (options.has("gui")) {
      runSparkServer();
    } else {
      processCommands();
    }
  }

  /**
   * Constructs a KD tree out of a set of stars and then reads and parses
   * user command line requests on the resulting tree.
   * @param starMap a hash map of all the stars in the input file.
   */
  private static void processCommands() {
    BufferedReader r;
    String s;

    try {
      r = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
    } catch (UnsupportedEncodingException e1) {
      System.out.println("ERROR: System does not support encoding");
      return;
    }
    Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

    try {
      while ((s = r.readLine()) != null) {
        if (s.length() == 0) {
          return;
        }
        Matcher matcher = pattern.matcher(s);
        List<String> l = new ArrayList<String>();
        while (matcher.find()) {
          l.add(matcher.group(0));
        }
        String[] fields = new String[l.size()];
        l.toArray(fields);
        if (fields[0] == null) {
          continue;
        } else {
          System.out.println(s);
          switch (fields[0]) {
            case "neighbors":
            case "radius": parseCommandInfo(fields);
              break;
            default: System.out.println("ERROR: Invalid command. Valid "
                + "commands are: radius r x y z, radius r \"name\", neighbors "
                + "k x y z, neighbors k \"name\".");
              break;
          }
        }
      }
    } catch (IOException e) {
      System.out.println("ERROR: Read failed.");
      return;
    }
  }

  /**
   * Parses a command line request to stars, extracts parameters,
   * and makes an appropriate function call.
   * @param starMap a map of all the stars in the input file.
   * @param starTree a KD tree of all the stars in the input file.
   * @param fields the unparsed input command as an array of strings.
   */
  private static void parseCommandInfo(String[] fields) {
    double rk;
    boolean containsName = false;

    double x;
    double y;
    double z;

    try {
      if (fields.length == LONG_COMMAND_LENGTH) {
        x = Double.parseDouble(fields[X_FIELD]);
        y = Double.parseDouble(fields[Y_FIELD]);
        z = Double.parseDouble(fields[Z_FIELD]);
      } else if (fields.length == SHORT_COMMAND_LENGTH) {
        String starName = fields[2];
        char[] nameChars = starName.toCharArray();
        containsName = true;
        if (nameChars[0] != '"') {
          System.out.println("ERROR: Star name must be preceded by quotes.");
          return;
        } else if (nameChars[nameChars.length - 1] != '"') {
          System.out.println("ERROR: Star name must be succeeded by quotes.");
          return;
        }
        starName = starName.substring(1, nameChars.length - 1);
        Star star = starMap.get(starName);
        if (star != null) {
          x = star.getX();
          y = star.getY();
          z = star.getZ();
        } else {
          System.out.println("ERROR: No star exists with name: " + fields[2]);
          return;
        }
      } else {
        argumentWarning(fields);
        return;
      }

      rk = Double.parseDouble(fields[1]);
    } catch (NumberFormatException e) {
      argumentWarning(fields);
      return;
    }

    Position pos = new Position(x, y, z);
    List<Cartesian> toPrint;

    if (fields[0].equals("neighbors")) {
      if (!(rk == Math.floor(rk))) {
        System.out.println("ERROR: k must be an integer value.");
        return;
      }
      if (rk < 0) {
        System.out.println("ERROR: k must be non-negative.");
        return;
      }
      toPrint = starTree.nearestNeighbors((int) (rk + 1), pos);
    } else if (fields[0].equals("radius")) {
      toPrint = starTree.radiusSearch(rk, pos);
    } else {
      argumentWarning(fields);
      return;
    }
    if (containsName) {
      toPrint.remove(0);
    } else if (fields[0].equals("neighbors") && toPrint.size() > rk) {
      toPrint.remove((int) rk);
    }

    Iterator<Cartesian> iter = toPrint.iterator();
    Cartesian curr;

    while (iter.hasNext()) {
      curr = iter.next();
      System.out.println(curr.toString());
    }

    return;
  }

  /**
   * Parses an input CSV star file and returns a hash set of the stars found
   * within.
   * @param db the CSV star file to parse
   * @return a hash set of star objects built from the CSV
   * @throws IOException on incorrect number of fields or read failure
   */
  private static HashMap<String, Star> parseFile(File db) throws IOException {
    BufferedReader r = new BufferedReader(new FileReader(db));
    starCol = new ArrayList<Cartesian>();

    String s = r.readLine();
    String[] fields = null;
    if (s != null) {
      fields = s.split(",");
    } else {
      System.out.println("ERROR: File was empty");
      r.close();
      throw new IllegalArgumentException();
    }
    if (fields.length != EXPECTED_FIELDS.length) {
      System.out.println("ERROR: Star file contained wrong number of fields.");
      r.close();
      throw new IllegalArgumentException();
    }

    for (int i = 0; i < fields.length; i++) {
      if (!fields[i].equals(EXPECTED_FIELDS[i])) {
        System.out.println("ERROR: Star file contained wrong fields.");
        r.close();
        throw new IllegalArgumentException();
      }
    }

    starMap = new HashMap<String, Star>();

    try {
      while ((s = r.readLine()) != null) {
        fields = s.split(",");
        if (fields.length != EXPECTED_FIELDS.length) {
          System.out.println("ERROR: Star file contained "
              + "wrong number of fields.");
          r.close();
          throw new IllegalArgumentException();
        }

        Star star = new Star(fields);

        if (star.getName() != null) {
          starMap.put(star.getName(), star);
        }
        starCol.add(star);
      }
    } catch (IOException e) {
      System.out.println("ERROR: Failed to read from file.");
      r.close();
      throw new IOException();
    }

    r.close();

    return starMap;
  }
  /**
   * Returns a template message about the set up of arguments input by the user.
   * @param fields the tokenized arguments input by the user.
   */
  private static void argumentWarning(String[] fields) {
    System.out.println("ERROR: Arguments must be in the form: "
        + fields[0] + " " + (fields[0].equals("neighbors") ? "k" : "r")
        + " x y z or " + fields[0] + " "
        + (fields[0].equals("neighbors") ? "k" : "r")
        + " name where x, y, and z are doubles"
        + " and " + (fields[0].equals("neighbors") ? "k" : "r")
        + " is an integer.");
  }

  /**
   * Launches a web site to allow convenient queries on a star database.
   */
  private static void runSparkServer() {
    Spark.setPort(PORT_NUMBER);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.get("/stars", new GetHandler(), new FreeMarkerEngine());
    Spark.post("/results", new RequestHandler(), new FreeMarkerEngine());
  }
  /**
   * Handles requests for the main web page at /stars.
   * @author is3
   *
   */
  private static class GetHandler implements TemplateViewRoute {

    /**
     * Creates the main web page to handle get requests at /stars.
     */
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Stars");
      return new ModelAndView(variables, "main.ftl");
    }
  }
  /**
   * Handles form posting for /results.
   * @author is3
   *
   */
  private static class RequestHandler implements TemplateViewRoute {

    /**
     * Handles form posting for /results.
     */
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      String requestType = qm.value("type");
      String knowName = qm.value("knowName");
      double x, y, z, rk;
      if (knowName.equals("false")) {
        try {
          x = Double.parseDouble(qm.value("x"));
          y = Double.parseDouble(qm.value("y"));
          z = Double.parseDouble(qm.value("z"));
          rk = Double.parseDouble(qm.value("rk"));
        } catch (NumberFormatException e) {
          String toReturn = "ERROR: Invalid coordinate entry.";
          Map<String, Object> variables = ImmutableMap.of(
              "title", "Error",
              "results", toReturn);
          return new ModelAndView(variables, "query.ftl");
        }
      } else if (knowName.equals("true")) {
        Star star = starMap.get(qm.value("starName"));
        if (star == null) {
          String toReturn = "ERROR: No star with that name.";
          Map<String, Object> variables = ImmutableMap.of(
              "title", "Error",
              "results", toReturn);
          return new ModelAndView(variables, "query.ftl");
        }
        x = star.getX();
        y = star.getY();
        z = star.getZ();
        try {
          rk = Double.parseDouble(qm.value("rk"));
        } catch (NumberFormatException e) {
          String toReturn = "ERROR: Field left blank.";
          Map<String, Object> variables = ImmutableMap.of(
              "title", "Results",
              "results", toReturn);
          return new ModelAndView(variables, "query.ftl");
        }
        if (rk <= 0) {
          String toReturn = "";
          Map<String, Object> variables = ImmutableMap.of(
              "title", "Results",
              "results", toReturn);
          return new ModelAndView(variables, "query.ftl");
        }
      } else {
        System.out.println("ERROR: Invalid form entry.");
        throw new IllegalArgumentException();
      }
      if (rk < 0) {
        String toReturn = "ERROR: Invalid search parameter.";
        Map<String, Object> variables = ImmutableMap.of(
            "title", "Error",
            "results", toReturn);
        return new ModelAndView(variables, "query.ftl");
      }
      List<Cartesian> toPrint;
      if (requestType.equals("radius")) {
        toPrint = starTree.radiusSearch(rk, new Position(x, y, z));
      } else if (requestType.equals("neighbors")) {
        toPrint = starTree.nearestNeighbors(
            (int) rk + 1,
            new Position(x, y, z));
      } else {
        System.out.println("ERROR: Invalid web request type.");
        throw new IllegalArgumentException();
      }

      if (knowName.equals("true")) {
        toPrint.remove(0);
      } else if (requestType.equals("neighbors") && toPrint.size() > rk) {
        toPrint.remove((int) rk);
      }
      String toReturn = "";
      StringBuilder sb = new StringBuilder();
      Iterator<Cartesian> iter = toPrint.iterator();
      Cartesian curr;

      while (iter.hasNext()) {
        curr = iter.next();
        sb.append("<p>" + curr.toString() + "</p>");
      }
      toReturn = sb.toString();

      Map<String, Object> variables = ImmutableMap.of(
          "title", "Results",
          "results", toReturn);
      return new ModelAndView(variables, "query.ftl");
    }

  }

}
