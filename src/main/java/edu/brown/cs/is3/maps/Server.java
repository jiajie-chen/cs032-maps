package edu.brown.cs.is3.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.is3.autocorrect.SuggestionHelper;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Server class hosting spark server.
 * @author is3
 *
 */
public class Server implements Runnable {
  private final Main m;
  private final int port;
  private final Gson gson = new Gson();
  private final SuggestionHelper sh;

  public Server(Main m, int port, SuggestionHelper sh) {
    this.m = m;
    this.port = port;
    this.sh = sh;
  }

  @Override
  public void run() {
    Spark.setPort(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.get("/maps", new GetHandler(), new FreeMarkerEngine());
    Spark.post("/suggestions", new SuggestionHandler());
    // Spark.get("/film/m/:id", new FilmHandler(), new FreeMarkerEngine());
    // Spark.get("/actor/m/:id", new ActorHandler(), new FreeMarkerEngine());
    // Spark.post("/results", new ResultsHandler());
  }

  /**
   * Handles requests for the main web page at /maps.
   * @author is3
   *
   */
  private static class GetHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Maps");
      return new ModelAndView(variables, "main.ftl");
    }
  }

  /**
   * Handles suggestion requests.
   * @author is3
   *
   */
  private class SuggestionHandler implements Route {

    @Override
    public Object handle(final Request req, final Response res) {
      QueryParamsMap qm = req.queryMap();
      List<String> rankedStart = new ArrayList<>();
      List<String> rankedEnd = new ArrayList<>();

      String qmStart = qm.value("inputStart");
      System.out.println("INPUT START: " + qmStart);
      if (qmStart != null) {
        String inputStart = gson.fromJson(qm.value("inputStart"), String.class);
        String[] startWords = { inputStart };

        rankedStart = sh.suggest(startWords);
      }

      String qmEnd = qm.value("inputEnd");
      System.out.println("INPUT END: " + qm.value("inputEnd"));
      if (qmEnd != null) {
        String inputEnd = gson.fromJson(qm.value("inputEnd"), String.class);
        String[] endWords = { inputEnd };

        rankedEnd = sh.suggest(endWords);
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("startSuggestions", rankedStart)
          .put("endSuggestions", rankedEnd).build();

      return gson.toJson(variables);
    }
  }
}

//
// private static Object generateErrorPage(String message) {
// // Map<String, Object> variables = new ImmutableMap.Builder<String,
// // Object>()
// // .put("results", rankedStart)
// // .put("endSuggestions", rankedEnd).build();
//
// return GSON.toJson(message);
// }
//
//
// /**
// * Handles actor queries.
// * @author is3
// *
// */
// private static class ResultsHandler implements Route {
// @Override
// public Object handle(final Request req, final Response res) {
// QueryParamsMap qm = req.queryMap();
// String inputStart = null;
// String inputEnd = null;
//
// String qmStart = qm.value("inputStart");
// System.out.println("INPUT START SUBMIT: " + qmStart);
// if (qmStart != null) {
// inputStart = GSON.fromJson(qm.value("inputStart"), String.class);
// }
//
// String qmEnd = qm.value("inputEnd");
// System.out.println("INPUT END SUBMIT: " + qm.value("inputEnd"));
// if (qmEnd != null) {
// inputEnd = GSON.fromJson(qm.value("inputEnd"), String.class);
// }
//
// if (inputStart == null || inputEnd == null) {
// return generateErrorPage("ERROR: Actor not found.");
// }
//
// List<ActorEdge> path = null;
//
// try {
// path = findPath(inputStart, inputEnd);
// } catch (RuntimeException | SQLException e) {
// System.err.println(e.getMessage());
// try {
// db = new Database(dbPath);
// } catch (ClassNotFoundException | SQLException e1) {
// System.err.println("ERROR: Encountered a database error "
// + "and could not recover.");
// throw new RuntimeException(e1);
// }
// return generateErrorPage(e.getMessage());
// }
//
// List<CompactActorEdge> results = new ArrayList<>();
// for (ActorEdge ae : path) {
// results.add(CompactActorEdge.toCompactEdge(ae));
// }
//
// Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
// .put("results", results).build();
// System.out.println("REPLYING WITH: " + results + "\n" + variables);
//
// return GSON.toJson(variables);
// }
// }
//
// /**
// * Handles individual film pages.
// * @author is3
// *
// */
// private static class FilmHandler implements TemplateViewRoute {
// @Override
// public ModelAndView handle(Request req, Response res) {
// String id = "/m/" + req.params(":id");
//
// Film f = filmOfId(id);
// String name = f.getName();
// Set<Actor> stars = f.getStars();
//
// StringBuilder results = new StringBuilder();
//
// for (Actor a : stars) {
// String actorId = a.getId();
// String actorName = a.getName();
// String link = "<p><a href=\"/actor" + actorId + "\">" + actorName
// + "</a></p>";
//
// results.append(link);
// }
//
// Map<String, Object> variables = ImmutableMap.of("title", name, "results",
// results.toString());
// return new ModelAndView(variables, "wiki.ftl");
// }
// }
//
// /**
// * Handles individual film pages.
// * @author is3
// *
// */
// private static class ActorHandler implements TemplateViewRoute {
// @Override
// public ModelAndView handle(Request req, Response res) {
// String id = "/m/" + req.params(":id");
//
// Actor a = actorOfId(id);
// String name = a.getName();
// Set<Film> films = a.getFilms();
//
// StringBuilder results = new StringBuilder();
//
// for (Film f : films) {
// String filmId = f.getId();
// String filmName = f.getName();
// String link = "<p><a href=\"/film" + filmId + "\">" + filmName
// + "</a></p>";
//
// results.append(link);
// }
//
// Map<String, Object> variables = ImmutableMap.of("title", name, "results",
// results.toString());
// return new ModelAndView(variables, "wiki.ftl");
// }
// }
//
// /**
// * Handles suggestion requests.
// * @author is3
// *
// */
// private static class SuggestionHandler implements Route {
//
// @Override
// public Object handle(final Request req, final Response res) {
// QueryParamsMap qm = req.queryMap();
// List<String> rankedStart = new ArrayList<>();
// List<String> rankedEnd = new ArrayList<>();
//
// String qmStart = qm.value("inputStart");
// System.out.println("INPUT START: " + qmStart);
// if (qmStart != null) {
// String inputStart = GSON.fromJson(qm.value("inputStart"), String.class);
// String[] startWords = { inputStart };
//
// rankedStart = sh.suggest(startWords);
// }
//
// String qmEnd = qm.value("inputEnd");
// System.out.println("INPUT END: " + qm.value("inputEnd"));
// if (qmEnd != null) {
// String inputEnd = GSON.fromJson(qm.value("inputEnd"), String.class);
// String[] endWords = { inputEnd };
//
// rankedEnd = sh.suggest(endWords);
// }
//
// Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
// .put("startSuggestions", rankedStart)
// .put("endSuggestions", rankedEnd).build();
//
// return GSON.toJson(variables);
// }
// }