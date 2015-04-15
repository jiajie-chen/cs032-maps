package edu.brown.cs.is3.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.is3.cartesian.RadianLatLng;
import edu.brown.cs.is3.graph.Path;
import edu.brown.cs.jc124.manager.MapsManager;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Server class hosting spark server for maps GUI implementation.
 * @author is3
 *
 */
public class Server implements Runnable {
  private final MapsManager m;
  private final int port;
  private final Gson gson = new Gson();
  private final Map<String, Double> traffic;
  private final Map<String, Double> changes;
  private static final double TILE_SIZE = .01;

  public Server(int port, Database db,
      Map<String, Double> traffic, Map<String, Double> changes) {

    this.m = new MapsManager(db);
    this.port = port;
    this.traffic = traffic;
    this.changes = changes;
  }

  @Override
  public void run() {
    Spark.setPort(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.get("/maps", new GetHandler(), new FreeMarkerEngine());
    Spark.post("/suggestions", new SuggestionHandler());
    Spark.post("/intersection", new IntersectionHandler());
    Spark.post("/point", new PointHandler());
    // Spark.post("/tile", new TileHandler());
    // Spark.get("/traffic", new GetHandler(), new FreeMarkerEngine());
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
   * Handles suggestion requests for auto correction.
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

        rankedStart = m.suggest(startWords);
      }

      String qmEnd = qm.value("inputEnd");
      System.out.println("INPUT END: " + qm.value("inputEnd"));
      if (qmEnd != null) {
        String inputEnd = gson.fromJson(qm.value("inputEnd"), String.class);
        String[] endWords = { inputEnd };

        rankedEnd = m.suggest(endWords);
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("startSuggestions", rankedStart)
          .put("endSuggestions", rankedEnd).build();

      return gson.toJson(variables);
    }
  }

  /**
   * Handles requests to find paths through an intersection.
   * @author is3
   *
   */
  private class IntersectionHandler implements Route {

    @Override
    public Object handle(final Request req, final Response res) {
      QueryParamsMap qm = req.queryMap();
      Path toReturn;

      String startStreet = qm.value("startStreet");
      String endStreet = qm.value("endStreet");
      String startCross = qm.value("startCross");
      String endCross = qm.value("endCross");

      System.out.println("Start: " + startStreet + " sCross: " + startCross
          + " End: " + endStreet + " eCross: " + endCross);

      if (startStreet != null && startCross != null
          && endStreet != null && endCross != null) {

        startStreet = gson.fromJson(qm.value("inputStart"), String.class);
        startCross = gson.fromJson(qm.value("inputStart"), String.class);
        endStreet = gson.fromJson(qm.value("inputStart"), String.class);
        endCross = gson.fromJson(qm.value("inputStart"), String.class);

        toReturn = m.getPathByIntersections(
            startStreet, startCross, endStreet, endCross, traffic);
      } else {
        // TODO
        toReturn = null;
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("path", toReturn).build();

      return gson.toJson(variables);
    }
  }

  /**
   * Handles requests to find paths through an intersection.
   * @author is3
   *
   */
  private class PointHandler implements Route {

    @Override
    public Object handle(final Request req, final Response res) {
      QueryParamsMap qm = req.queryMap();
      Path toReturn;

      String start = qm.value("start");
      String end = qm.value("end");

      System.out.println("Start: " + start + " End: " + end);

      if (start != null && end != null) {

        RadianLatLng startPoint =
            gson.fromJson(qm.value("inputStart"), RadianLatLng.class);
        RadianLatLng endPoint =
            gson.fromJson(qm.value("inputStart"), RadianLatLng.class);

        toReturn = m.getPathByPoints(
            startPoint.getLat(), startPoint.getLng(),
            endPoint.getLat(), endPoint.getLng(),
            traffic);
      } else {
        // TODO
        toReturn = null;
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("path", toReturn).build();

      return gson.toJson(variables);
    }
  }
}