package edu.brown.cs.jc124.bacon;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import edu.brown.cs.jc124.autocorrect.AutoCorrectionParser;
import edu.brown.cs.jc124.autocorrect.AutoCorrector;
import edu.brown.cs.jc124.autocorrect.AutoCorrector.Builder;
import edu.brown.cs.jc124.bacon.graph.BaconGraph;
import edu.brown.cs.jc124.data.Trie;
import edu.brown.cs.jc124.util.MainRunnable;
import freemarker.template.Configuration;

/**
 * @author jchen
 *
 *         A class for managing the GUI server of the querier.
 */
public final class SparkServer implements MainRunnable {
  private int port;
  private static final Gson GSON = new Gson();
  private BaconManager bacon;
  private AutoCorrector auto;

  private static final int LEV_DIST = 10;

  /**
   * Makes a new server that contains queries in the db file.
   *
   * @param port
   *          the port to run on
   * @param bacon
   *          the manager to query from
   */
  public SparkServer(int port, BaconManager bacon) {
    this.port = port;
    this.bacon = bacon;

    this.auto = initAutoCorrect();
  }

  private AutoCorrector initAutoCorrect() {
    Trie<String> trie = new Trie<>();
    BaconGraph g = bacon.getGraph();

    Set<String> allIds = g.getAllNodeKeys();
    for (String id : allIds) {
      String name = g.getNodeAttribute(id).getAttribute().getName();
      trie.add(name);
    }

    bacon.clear();

    Builder b = new AutoCorrector.Builder();
    b.addPrefixSuggester(trie);
    b.addLevenshteinSuggester(trie, LEV_DIST);
    b.setSmartRanker();

    return b.build();
  }

  /**
   * Runs the GUI server on the port given in the constructor.
   */
  @Override
  public void runMain() {
    Spark.setPort(port);

    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes

    // front end
    Spark.get("/home/", new HomeHandler(), freeMarker);
    Spark.get("/actor/", new ActorHandler(), freeMarker);
    Spark.get("/movie/", new MovieHandler(), freeMarker);

    // API queries
    Spark.get("/autocorrect/:phrase/", new AutoResultsHandler());
    Spark.get("/actor/:id/", new ActorResultsHandler());
    Spark.get("/movie/:id/", new MovieResultsHandler());
    Spark.get("/bacon/:start/:end/", new HomeResultsHandler());
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
  private class HomeHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Bacon");
      return new ModelAndView(variables, "home.ftl");
    }
  }

  /**
   * @author jchen
   *
   * Actor handler for the front end page.
   */
  private class ActorHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Bacon");
      return new ModelAndView(variables, "actor.ftl");
    }
  }

  /**
   * @author jchen
   *
   * Movie handler for the front end page.
   */
  private class MovieHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Autocorrect");
      return new ModelAndView(variables, "movie.ftl");
    }
  }

  /**
   * @author jchen Helper class for storing and formulating json responses.
   */
  private static final class JsonResponse {
    private static String baconToJson(Iterable<String> results) {
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

    private static String autoToJson(List<String> results) {
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
   * @author jchen
   *
   * Handles all queries for autocorrection. Generates a Json response.
   */
  private class AutoResultsHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      String in = req.params(":phrase");
      try {
        in = URLDecoder.decode(in, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        in = "";
      }

      List<String> results = AutoCorrectionParser.queryAutocorrect(auto, in);
      List<String> top3 = new ArrayList<>();
      for (int i = 0; i < Math.min(3, results.size()); i++) {
        top3.add(results.get(i));
      }
      return JsonResponse.autoToJson(top3);
    }
  }

  /**
   * @author jchen
   *
   * Handles all queries for bacon. Generates a Json response.
   */
  private class HomeResultsHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      String in = req.params(":phrase");

      List<String> results = AutoCorrectionParser.queryAutocorrect(auto, in);
      return JsonResponse.autoToJson(results);
    }
  }

  /**
   * @author jchen
   *
   * Handles all queries for actor. Generates a Json response.
   */
  private class ActorResultsHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      String in = req.params(":phrase");

      List<String> results = AutoCorrectionParser.queryAutocorrect(auto, in);
      return JsonResponse.autoToJson(results);
    }
  }

  /**
   * @author jchen
   *
   * Handles all queries for movie. Generates a Json response.
   */
  private class MovieResultsHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      String in = req.params(":phrase");

      List<String> results = AutoCorrectionParser.queryAutocorrect(auto, in);
      return JsonResponse.autoToJson(results);
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
