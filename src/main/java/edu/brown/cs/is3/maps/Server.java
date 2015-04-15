package edu.brown.cs.is3.maps;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.brown.cs.is3.cartesian.RadianLatLng;
import edu.brown.cs.is3.cartesian.Tile;
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

  /**
   * Builds a server.
   * @param port to listen on.
   * @param db to search data.
   * @param traffic synchronized traffic map to store and search traffic.
   *        Updated by another thread.
   * @param changes synchronized changes map to store changes to traffic.
   *        Updated by another thread.
   */
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
    Spark.post("/tile", new TileHandler());
    // Spark.get("/traffic", new GetHandler(), new FreeMarkerEngine());
    Spark.post("/changes", new ChangesHandler());
  }

  /**
   * is3 Handles requests for the main web page at /maps.
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

        startStreet = gson.fromJson(qm.value("startStreet"), String.class);
        startCross = gson.fromJson(qm.value("startCross"), String.class);
        endStreet = gson.fromJson(qm.value("endStreet"), String.class);
        endCross = gson.fromJson(qm.value("endCross"), String.class);

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

      String sLat = qm.value("startLat");
      String eLat = qm.value("endLat");
      String sLng = qm.value("startLng");
      String eLng = qm.value("endLng");

      System.out.println("Start[ Lat: " + sLat + " Lng: " + sLng
          + "] End[ Lat: " + eLat + " Lng: " + eLng + "]");

      if (sLat != null && sLng != null
          && eLat != null && eLng != null) {

        double startLat = gson.fromJson(qm.value("startLat"), Double.class);
        double startLng = gson.fromJson(qm.value("startLng"), Double.class);
        double endLat = gson.fromJson(qm.value("endLat"), Double.class);
        double endLng = gson.fromJson(qm.value("endLng"), Double.class);

        toReturn = m.getPathByPoints(
            startLat, startLng,
            endLat, endLng,
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

  /**
   * Handles tile requests for map display.
   * @author is3
   *
   */
  private class TileHandler implements Route {

    @Override
    public Object handle(final Request req, final Response res) {
      QueryParamsMap qm = req.queryMap();
      String jsonTiles = qm.value("tiles");

      Type listType = new TypeToken<ArrayList<RadianLatLng>>() {
      }.getType();
      List<RadianLatLng> nwCorners = new Gson().fromJson(jsonTiles, listType);

      ImmutableMap.Builder<String, Tile> tiles = new ImmutableMap.Builder<String, Tile>();
      for (RadianLatLng c : nwCorners) {
        String tID = c.getLat() + ":" + c.getLng();
        tiles.put(tID, m.getTile(c, TILE_SIZE));
      }
      Map<String, Tile> toSend = tiles.build();

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("tiles", toSend).build();

      return gson.toJson(variables);
    }
  }

  /**
   * Handles suggestion requests for auto correction.
   * @author is3
   *
   */
  private class ChangesHandler implements Route {

    @Override
    public Object handle(final Request req, final Response res) {
      QueryParamsMap qm = req.queryMap();
      Map<String, Double> toSend = ImmutableMap.copyOf(changes);
      changes.clear();

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("changes", toSend).build();

      return gson.toJson(variables);
    }
  }
}
