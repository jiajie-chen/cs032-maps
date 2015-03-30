package edu.brown.cs.jc124.autocorrect;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import edu.brown.cs.jc124.util.MainRunnable;
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
public final class SparkServer implements MainRunnable {
  private static final Gson GSON = new Gson();
  private AutoCorrector auto;
  private List<File> db;

  /**
   * Makes a new server that contains the corpora in the db files.
   *
   * @param auto
   *          the autocorrector to query from
   * @param db
   *          the files the DB was generated from
   */
  public SparkServer(AutoCorrector auto, List<File> db) {
    this.auto = auto;
    this.db = db;
  }

  /**
   * Runs the GUI server on port 2345.
   */
  @Override
  public void runMain() {
    Spark.setPort(4567);

    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes

    // front end
    Spark.get("/autocorrect/", new FrontHandler(), freeMarker);

    // query
    Spark.get("/autocorrect/:phrase/", new ResultsHandler());
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
   * @author jchen Main handler for the front end page.
   */
  private class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Autocorrect",
          "db", db.toString());
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * @author jchen Helper class for storing and formulating json responses.
   */
  private static final class JsonResponse {
    private static String toJson(List<String> results) {
      List<String> str = new ArrayList<String>();

      for (String s : results) {
        str.add(s);
      }

      JsonObject res = new JsonObject();

      Type listType = new TypeToken<List<String>>() {
      }.getType();
      res.add("suggestions", GSON.toJsonTree(str, listType));
      return GSON.toJson(res);
    }
  }

  /**
   * @author jchen Handles all queries for autocorrection. Generates a Json
   *         response.
   */
  private class ResultsHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      String in = req.params(":phrase");

      List<String> results = AutoCorrectionParser.queryAutocorrect(auto, in);
      return JsonResponse.toJson(results);
    }
  }

  /**
   * @author jchen Handles java exceptions.
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

      e.printStackTrace();
    }
  }
}
