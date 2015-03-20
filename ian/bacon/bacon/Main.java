package edu.brown.cs.is3.bacon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.is3.autocorrect.SuggestionHelper;
import edu.brown.cs.is3.graph.ActorSearch;
import edu.brown.cs.is3.graph.FirstLastChecker;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Main class for implementing bacon. Handles CLI and gui requests and queries
 * database of films to find shortest paths.
 * @author is3
 *
 */
public abstract class Main {
  private static int sparkPort;
  private static String[] args;
  private static String dbPath;
  private static Database db;
  private static SuggestionHelper sh;
  private static final Gson GSON = new Gson();
  private static final int DEFAULT_PORT = 3141;
  private static final int EXPECTED_ARGS = 3;
  private static final int EXPECTED_GUI_ARGS = 1;

  /**
   * Main method. Forwards args to run.
   * @param args passed by CLI
   */
  public static void main(String[] args) {
    Main.args = args;
    try {
      run();
    } catch (ClassNotFoundException | SQLException e) {
      if (db != null) {
        db.close();
      }
      System.err.println("ERROR: " + e.getMessage());
    }

    return;
  }

  /**
   * Core method for main class. Parses args and launches gui or responds to
   * queries.
   *
   * @throws ClassNotFoundException on class for name failure.
   * @throws SQLException on database error.
   */
  private static void run() throws ClassNotFoundException, SQLException {
    OptionParser parser = new OptionParser();

    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSpec<String> argsSpec = parser.nonOptions().ofType(String.class);
    OptionSet options;

    try {
      options = parser.parse(args);
    } catch (OptionException e) {
      System.err.println("ERROR: Bad options.");
      return;
    }

    List<String> argsList = new ArrayList<String>();

    if (options.has("port")) {
      sparkPort = (int) options.valueOf("port");
    } else {
      sparkPort = DEFAULT_PORT;
    }

    try {
      argsList = options.valuesOf(argsSpec);
    } catch (OptionException e) {
      printUsage();
      return;
    }

    if (options.has("gui")) {
      if (argsList.size() != EXPECTED_GUI_ARGS) {
        printUsage();
        return;
      } else {
        dbPath = argsList.get(0);
        db = new Database(dbPath);

        try {
          sh = new SuggestionHelper();
          sh.fill(db);
        } catch (RuntimeException e) {
          System.err.println(e);
          db.close();
          return;
        }
      }

      runSparkServer();
    } else {
      if (argsList.size() != EXPECTED_ARGS) {
        printUsage();
        return;
      } else {
        String start = argsList.get(0);
        String end = argsList.get(1);
        dbPath = argsList.get(2);
        db = new Database(dbPath);

        try {
          printPath(start, end);
        } catch (Exception e) {
          db.close();
          System.err.println("ERROR: " + e.getMessage());
          return;
        }
      }
    }
  }

  /**
   * Prints path from start actor to end actor.
   * @param start name of starting actor.
   * @param end name of ending actor.
   * @throws ClassNotFoundException on class for name error.
   * @throws SQLException on database error.
   */
  private static void printPath(String start, String end)
      throws ClassNotFoundException, SQLException {

    List<ActorEdge> toPrint = findPath(start, end);
    if (toPrint.isEmpty()) {
      System.out.println(start + " -/- " + end);
    }

    for (ActorEdge ae : toPrint) {
      System.out.println(ae);
    }
  }

  /**
   * Finds path from start actor to end actor.
   * @param start name of starting actor.
   * @param end name of ending actor.
   * @return a list of the edges connecting the two actors.
   * @throws ClassNotFoundException on class for name error.
   * @throws SQLException on database error.
   */
  private static List<ActorEdge> findPath(String start, String end)
      throws SQLException {

    if (start == null || end == null) {
      throw new RuntimeException("ERROR: Searching for null actor.");
    }

    if (start.equals(end)) {
      throw new RuntimeException("Those are the same actor, silly!");
    }

    ActorVertex startAct = new ActorVertex(actorOfName(start), 0);
    ActorVertex endAct = new ActorVertex(actorOfName(end), Double.MAX_VALUE);

    ActorSearch s = new ActorSearch();
    return s.dijkstras(startAct, endAct, new FirstLastChecker());
  }

  /**
   * Prints program usage.
   */
  private static void printUsage() {
    System.out.println("ERROR: Usage: ./run "
        + "  \"<name1>\" \"<name2>\" <db> OR "
        + "./run [--port <int>] --gui <db>");
    return;
  }

  /**
   * Builds an actor from an id.
   * @param id of actor to build.
   * @return the actor.
   */
  static Actor actorOfId(String id) {
    return db.actorOfId(id);
  }

  /**
   * Builds a film from an id.
   * @param id of film to build.
   * @return the film.
   */
  public static Film filmOfId(String id) {
    return db.filmOfId(id);
  }

  /**
   * Produces the set of films starred in by an actor.
   * @param actor to examine.
   * @return set of films containing actor.
   */
  public static Set<Film> filmsOfActor(Actor actor) {
    return db.filmsOfActor(actor);
  }

  /**
   * Produces set of actors in a film.
   * @param film to examine.
   * @return set of actors in film.
   */
  public static Set<Actor> starsOfFilm(Film film) {
    return db.starsOfFilm(film);
  }

  /**
   * Produces an actor from a name.
   * @param name of actor to make.
   * @return the actor.
   */
  public static Actor actorOfName(String name) {
    return db.actorOfName(name);
  }

  /**
   * Runs a gui implementation of bacon which supports querying and
   * autocorrecting.
   */
  private static void runSparkServer() {
    Spark.setPort(sparkPort);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.get("/home", new GetHandler(), new FreeMarkerEngine());
    Spark.get("/film/m/:id", new FilmHandler(), new FreeMarkerEngine());
    Spark.get("/actor/m/:id", new ActorHandler(), new FreeMarkerEngine());
    Spark.post("/suggestions", new SuggestionHandler());
    Spark.post("/results", new ResultsHandler());
  }

  private static Object generateErrorPage(String message) {
    // Map<String, Object> variables = new ImmutableMap.Builder<String,
    // Object>()
    // .put("results", rankedStart)
    // .put("endSuggestions", rankedEnd).build();

    return GSON.toJson(message);
  }

  /**
   * Handles requests for the main web page at /home.
   * @author is3
   *
   */
  private static class GetHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Bacon");
      return new ModelAndView(variables, "main.ftl");
    }
  }

  /**
   * Handles actor queries.
   * @author is3
   *
   */
  private static class ResultsHandler implements Route {
    @Override
    public Object handle(final Request req, final Response res) {
      QueryParamsMap qm = req.queryMap();
      String inputStart = null;
      String inputEnd = null;

      String qmStart = qm.value("inputStart");
      System.out.println("INPUT START SUBMIT: " + qmStart);
      if (qmStart != null) {
        inputStart = GSON.fromJson(qm.value("inputStart"), String.class);
      }

      String qmEnd = qm.value("inputEnd");
      System.out.println("INPUT END SUBMIT: " + qm.value("inputEnd"));
      if (qmEnd != null) {
        inputEnd = GSON.fromJson(qm.value("inputEnd"), String.class);
      }

      if (inputStart == null || inputEnd == null) {
        return generateErrorPage("ERROR: Actor not found.");
      }

      List<ActorEdge> path = null;

      try {
        path = findPath(inputStart, inputEnd);
      } catch (RuntimeException | SQLException e) {
        System.err.println(e.getMessage());
        try {
          db = new Database(dbPath);
        } catch (ClassNotFoundException | SQLException e1) {
          System.err.println("ERROR: Encountered a database error "
              + "and could not recover.");
          throw new RuntimeException(e1);
        }
        return generateErrorPage(e.getMessage());
      }

      List<CompactActorEdge> results = new ArrayList<>();
      for (ActorEdge ae : path) {
        results.add(CompactActorEdge.toCompactEdge(ae));
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("results", results).build();
      System.out.println("REPLYING WITH: " + results + "\n" + variables);

      return GSON.toJson(variables);
    }
  }

  /**
   * Handles individual film pages.
   * @author is3
   *
   */
  private static class FilmHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String id = "/m/" + req.params(":id");

      Film f = filmOfId(id);
      String name = f.getName();
      Set<Actor> stars = f.getStars();

      StringBuilder results = new StringBuilder();

      for (Actor a : stars) {
        String actorId = a.getId();
        String actorName = a.getName();
        String link = "<p><a href=\"/actor" + actorId + "\">" + actorName
            + "</a></p>";

        results.append(link);
      }

      Map<String, Object> variables = ImmutableMap.of("title", name, "results",
          results.toString());
      return new ModelAndView(variables, "wiki.ftl");
    }
  }

  /**
   * Handles individual film pages.
   * @author is3
   *
   */
  private static class ActorHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      String id = "/m/" + req.params(":id");

      Actor a = actorOfId(id);
      String name = a.getName();
      Set<Film> films = a.getFilms();

      StringBuilder results = new StringBuilder();

      for (Film f : films) {
        String filmId = f.getId();
        String filmName = f.getName();
        String link = "<p><a href=\"/film" + filmId + "\">" + filmName
            + "</a></p>";

        results.append(link);
      }

      Map<String, Object> variables = ImmutableMap.of("title", name, "results",
          results.toString());
      return new ModelAndView(variables, "wiki.ftl");
    }
  }

  /**
   * Handles suggestion requests.
   * @author is3
   *
   */
  private static class SuggestionHandler implements Route {

    @Override
    public Object handle(final Request req, final Response res) {
      QueryParamsMap qm = req.queryMap();
      List<String> rankedStart = new ArrayList<>();
      List<String> rankedEnd = new ArrayList<>();

      String qmStart = qm.value("inputStart");
      System.out.println("INPUT START: " + qmStart);
      if (qmStart != null) {
        String inputStart = GSON.fromJson(qm.value("inputStart"), String.class);
        String[] startWords = { inputStart };

        rankedStart = sh.suggest(startWords);
      }

      String qmEnd = qm.value("inputEnd");
      System.out.println("INPUT END: " + qm.value("inputEnd"));
      if (qmEnd != null) {
        String inputEnd = GSON.fromJson(qm.value("inputEnd"), String.class);
        String[] endWords = { inputEnd };

        rankedEnd = sh.suggest(endWords);
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("startSuggestions", rankedStart)
          .put("endSuggestions", rankedEnd).build();

      return GSON.toJson(variables);
    }
  }
}
