package edu.brown.cs.jc124.stars;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import freemarker.template.Configuration;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * @author jchen
 *
 *         A class for managing the GUI server interface of the querier.
 */
public final class SparkServer {
  private static final Gson GSON = new Gson();
  private StarDataManager stars;
  private File db;

  /**
   * Makes a new server that contains the stars in the file db.
   *
   * @param stars
   *          the stars to query from
   * @param db
   *          the file the DB was generated from
   */
  public SparkServer(StarDataManager stars, File db) {
    this.stars = stars;
    this.db = db;
  }

  /**
   * runs the GUI server on port 2345.
   */
  public void runSparkServer() {
    Spark.setPort(2345);

    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes

    // front end
    Spark.get("/", new FrontHandler(), freeMarker);

    // RESTful API
    Spark.get("/name/:command/:kr/:starname", new NameResultsHandler());
    Spark.get("/coord/:command/:kr/:x/:y/:z", new LocationResultsHandler());
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.\n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * @author jchen
   * Main handler for the front end page.
   */
  private class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Stars", "db",
          db.toString());
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * @author jchen
   * Helper class for storing and formulating json responses.
   */
  private static final class StarJsonResponse {
    // these are only for serialization, so keep unused
    @SuppressWarnings("unused")
    private int id;
    @SuppressWarnings("unused")
    private String name;

    private StarJsonResponse(int id, String name) {
      this.id = id;
      this.name = name;
    }

    private static String toStarJson(List<Star> results) {
      List<StarJsonResponse> ids = new ArrayList<StarJsonResponse>();

      for (Star s : results) {
        ids.add(new StarJsonResponse(s.getId(), s.getName()));
      }

      Type listType = new TypeToken<List<StarJsonResponse>>() {}.getType();

      JsonObject res = new JsonObject();
      res.addProperty("success", true);
      res.add("stars", GSON.toJsonTree(ids, listType));
      return GSON.toJson(res);
    }

    private static String toErrorJson(String msg) {
      JsonObject err = new JsonObject();
      err.addProperty("success", false);
      err.addProperty("error", msg);
      return GSON.toJson(err);
    }
  }

  /**
   * @author jchen
   * Handles name based queries.
   */
  private class NameResultsHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      String command = req.params(":command");
      String kr = req.params(":kr");
      String name = req.params(":starname");

      // wrap name in quotes to adapt to nameCommand name parameter
      name = "\"" + name + "\"";

      // try to create response, return error message if exception
      try {
        List<Star> results = stars.nameCommand(command, kr, name);
        return StarJsonResponse.toStarJson(results);
      } catch (NoSuchElementException | IllegalArgumentException
          | IndexOutOfBoundsException e) {
        // only errors caused by parsing or searching tree
        return StarJsonResponse.toErrorJson(e.getMessage());
      }
    }
  }

  /**
   * @author jchen
   * Handles location based queries.
   */
  private class LocationResultsHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      String command = req.params(":command");
      String kr = req.params(":kr");
      String x = req.params(":x");
      String y = req.params(":y");
      String z = req.params(":z");

      // try to create response, return error message if exception
      try {
        List<Star> results = stars.locationCommand(command, kr, x, y, z);
        return StarJsonResponse.toStarJson(results);
      } catch (NoSuchElementException | IllegalArgumentException
          | IndexOutOfBoundsException e) {
        // only errors caused by parsing or searching tree
        return StarJsonResponse.toErrorJson(e.getMessage());
      }
    }
  }

  /**
   * @author jchen
   * Handles java exceptions.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
