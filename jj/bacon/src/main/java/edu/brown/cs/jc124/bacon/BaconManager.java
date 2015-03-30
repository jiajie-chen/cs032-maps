package edu.brown.cs.jc124.bacon;

import java.sql.SQLException;

import edu.brown.cs.jc124.bacon.graph.Actor;
import edu.brown.cs.jc124.bacon.graph.BaconGraph;
import edu.brown.cs.jc124.bacon.graph.BaconPathFinder;
import edu.brown.cs.jc124.bacon.graph.BaconRepository;
import edu.brown.cs.jc124.bacon.graph.Movie;
import edu.brown.cs.jc124.bacon.graph.SqlBaconRepository;
import edu.brown.cs.jc124.data.Graph;
import edu.brown.cs.jc124.data.PathFinder;
import edu.brown.cs.jc124.data.RepositoryException;
import edu.brown.cs.jc124.data.UndirectedGraph;

/**
 * @author jchen
 *
 *         Manager for the Bacon querier. Contains the repository for Bacon and
 *         pathfinder.
 *
 */
public final class BaconManager implements AutoCloseable {
  private BaconRepository repo;
  private BaconGraph g;
  private PathFinder<String, Actor, Movie> find;

  /**
   * @author jchen
   *
   *         Utility class for building a manager.
   *
   */
  public static class Builder {
    private BaconManager bacon;
    private Graph<String, Actor, Movie> intern;

    /**
     * Starts a new builder.
     */
    public Builder() {
      bacon = new BaconManager();
      intern = null;
    }

    /**
     * Sets the manager's pathfinder implementation.
     *
     * @param find
     *          the pathfinder to use
     * @return the builder, for chaining commands
     */
    public Builder setPathFinder(PathFinder<String, Actor, Movie> find) {
      bacon.find = find;
      return this;
    }

    /**
     * Sets the pathfinder to the basic BaconPathFinder.
     *
     * @return the builder, for chaining commands
     */
    public Builder setDefaultPathFinder() {
      bacon.find = new BaconPathFinder();
      return this;
    }

    /**
     * Sets the bacon repository to a repo.
     *
     * @param repo
     *          the repository to use
     * @return the builder, for chaining commands
     */
    public Builder setBaconRepository(BaconRepository repo) {
      bacon.repo = repo;
      return this;
    }

    /**
     * Sets repository to new SQL database.
     *
     * @param db
     *          the database url
     * @return the builder, for chaining commands
     */
    public Builder setSqlBaconRepository(String db) {
      try {
        bacon.repo = new SqlBaconRepository(db);
      } catch (ClassNotFoundException | SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
      return this;
    }

    /**
     * Sets the internal graph used for caching data.
     *
     * @param in
     *          the graph to use for interning data
     * @return the builder, for chaining commands
     */
    public Builder setInternGraph(Graph<String, Actor, Movie> in) {
      this.intern = in;
      return this;
    }

    /**
     * Sets the interning graph to a default undirected graph.
     *
     * @return the builder, for chaining commands
     */
    public Builder setDefaultInternGraph() {
      this.intern = new UndirectedGraph<>();
      return this;
    }

    /**
     * Builds a new manager using the past settings. Must have set a pathfinder,
     * repo, and interner.
     *
     * @return a manager using the settings provided
     */
    public BaconManager build() {
      if (bacon.repo == null || bacon.find == null || this.intern == null) {
        throw new IllegalArgumentException(
            "Builder does not have enough options set");
      }

      bacon.g = new BaconGraph(bacon.repo, intern);

      return bacon;
    }

    /**
     * Builds manager using all the default command.
     *
     * @param db
     *          the SQL database url
     * @return a defualt manager
     */
    public BaconManager buildDefault(String db) {
      setDefaultPathFinder();
      setDefaultInternGraph();
      setSqlBaconRepository(db);
      return build();
    }
  }

  private BaconManager() {
    repo = null;
    g = null;
    find = null;
  }

  /**
   * Finds a path from actor1 to actor2 in the graph.
   *
   * @param actor1
   *          the full name of the starting actor
   * @param actor2
   *          the full name of the ending actor
   * @return null if there is no path, otherwise the path's nodes from actor1 to
   *         actor2
   */
  public Iterable<String> query(String actor1, String actor2) {
    String id1 = repo.getActorId(actor1);
    if (id1 == null) {
      throw new IllegalArgumentException(actor1 + " not in database");
    }

    String id2 = repo.getActorId(actor2);
    if (id2 == null) {
      throw new IllegalArgumentException(actor2 + " not in database");
    }

    Iterable<String> path = find.findPath(g, id1, id2);

    return path;
  }

  /**
   * Gets the BaconGraph that the manager uses for pathfinding.
   *
   * @return the graph
   */
  public BaconGraph getGraph() {
    return g;
  }

  /**
   * Clears caches for the graph and repository used by the manager.
   *
   * @return true if there was data cloned
   */
  public boolean clear() {
    return g.clear() || repo.clear();
  }

  @Override
  public void close() throws Exception {
    repo.close();
    clear();
  }
}
