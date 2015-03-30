package edu.brown.cs.jc124.bacon.graph;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import edu.brown.cs.jc124.data.Graph.EdgeAttribute;
import edu.brown.cs.jc124.data.Graph.NodeAttribute;
import edu.brown.cs.jc124.data.RepositoryException;

/**
 * @author jchen
 *
 *         Bacon repository that queries a SQL database.
 */
public class SqlBaconRepository extends BaconRepository implements
    AutoCloseable {
  /**
   * @author jchen
   *
   *         Node wrapper for actors.
   */
  private final class ActorAttribute implements NodeAttribute<Actor> {
    private Actor a;

    private ActorAttribute(Actor a) {
      this.a = a;
    }

    @Override
    public Actor getAttribute() {
      return a;
    }
  }

  /**
   * @author jchen
   *
   *         Edge wrapper for movies.
   */
  private final class MovieAttribute implements EdgeAttribute<Movie> {
    private Movie m;

    private MovieAttribute(Movie m) {
      this.m = m;
    }

    @Override
    public Movie getAttribute() {
      return m;
    }

    @Override
    public double getEdgeWeight() {
      return 1.0 / m.getActorIds().size();
    }
  }

  private Connection conn;

  /**
   * Makes a Bacon graph source repository from a path to a sqlite db file.
   *
   * @param db
   *          Path to the db file
   * @throws ClassNotFoundException
   *           if opening the DB failed
   * @throws SQLException
   *           if opening the DB failed
   */
  public SqlBaconRepository(String db) throws ClassNotFoundException,
      SQLException {
    Class.forName("org.sqlite.JDBC");
    String url = "jdbc:sqlite:" + db;
    conn = DriverManager.getConnection(url);
    Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys = ON;");
    stat.close();
  }

  @Override
  public Set<String> getAdjacentNodes(String nodeKey) {
    String query = "SELECT b.actor FROM" + " actor_film as a, actor_film as b"
        + " WHERE" + "  a.film = b.film" + "  AND"
        + "  a.actor = ? AND b.actor != a.actor" + " GROUP BY b.actor;";

    PreparedStatement prep = null;
    ResultSet rs = null;
    Set<String> toReturn = null;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, nodeKey);

      rs = prep.executeQuery();
      toReturn = new HashSet<String>();
      while (rs.next()) {
        toReturn.add(rs.getString(1));
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    return toReturn;
  }

  @Override
  public NodeAttribute<Actor> getNodeAttribute(String nodeKey) {
    return new ActorAttribute(getActor(nodeKey));
  }

  private String getEdgeId(String begin, String end) {
    String query = "SELECT b.film FROM" + " actor_film as a, actor_film as b"
        + " WHERE" + "  a.film = b.film" + "  AND"
        + "  a.actor = ? AND b.actor = ?" + " GROUP BY b.film" + " LIMIT 1;";

    PreparedStatement prep = null;
    ResultSet rs = null;
    String id = null;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, begin);
      prep.setString(2, end);

      rs = prep.executeQuery();
      if (rs.next()) {
        id = rs.getString(1);
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    // no id found
    if (id == null) {
      return null;
    }
    return id;
  }

  @Override
  public EdgeAttribute<Movie> getEdgeAttribute(String begin, String end) {
    String id = getEdgeId(begin, end);

    // no id found
    if (id == null) {
      return null;
    }
    return getEdgeAttribute(id);
  }

  @Override
  public EdgeAttribute<Movie> getEdgeAttribute(String edgeKey) {
    return new MovieAttribute(getMovie(edgeKey));
  }

  @Override
  public Set<String> getAllNodeKeys() {
    String query = "SELECT id FROM actor;";

    PreparedStatement prep = null;
    ResultSet rs = null;
    Set<String> toReturn = null;
    try {
      prep = conn.prepareStatement(query);

      rs = prep.executeQuery();
      toReturn = new HashSet<String>();
      while (rs.next()) {
        toReturn.add(rs.getString(1));
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    return toReturn;
  }

  @Override
  public Set<String> getAllEdgeKeys() {
    String query = "SELECT id FROM film;";

    PreparedStatement prep = null;
    ResultSet rs = null;
    Set<String> toReturn = null;
    try {
      prep = conn.prepareStatement(query);

      rs = prep.executeQuery();
      toReturn = new HashSet<String>();
      while (rs.next()) {
        toReturn.add(rs.getString(1));
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    return toReturn;
  }

  @Override
  public boolean hasNode(String nodeKey) {
    String query = "SELECT id FROM actor WHERE id = ?;";

    PreparedStatement prep = null;
    ResultSet rs = null;
    boolean toReturn = false;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, nodeKey);

      rs = prep.executeQuery();
      toReturn = rs.next();
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    return toReturn;
  }

  @Override
  public boolean hasEdge(String begin, String end) {
    return getEdgeId(begin, end) != null;
  }

  @Override
  public boolean hasEdge(String edgeKey) {
    String query = "SELECT id FROM film WHERE id = ?;";

    PreparedStatement prep = null;
    ResultSet rs = null;
    boolean toReturn = false;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, edgeKey);

      rs = prep.executeQuery();
      toReturn = rs.next();
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    return toReturn;
  }

  @Override
  public Actor getActorFromSource(String id) {
    String query = "SELECT name FROM actor" + " WHERE id = ?;";

    PreparedStatement prep = null;
    ResultSet rs = null;
    String name = null;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, id);

      rs = prep.executeQuery();
      if (rs.next()) {
        name = rs.getString(1);
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    query = "SELECT f.id FROM actor_film as af, film as f"
        + " WHERE af.actor = ? AND af.film = f.id;";

    prep = null;
    rs = null;
    Set<String> movieIds = null;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, id);

      rs = prep.executeQuery();
      movieIds = new HashSet<String>();
      while (rs.next()) {
        movieIds.add(rs.getString(1));
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    if (name == null) {
      return null;
    }

    return new Actor(name, movieIds);
  }

  @Override
  public Movie getMovieFromSource(String id) {
    String query = "SELECT name FROM film" + " WHERE id = ?;";

    PreparedStatement prep = null;
    ResultSet rs = null;
    String name = null;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, id);

      rs = prep.executeQuery();
      if (rs.next()) {
        name = rs.getString(1);
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    query = "SELECT a.id FROM actor_film as af, actor as a"
        + " WHERE af.film = ? AND af.actor = a.id;";

    prep = null;
    rs = null;
    Set<String> actorIds = null;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, id);

      rs = prep.executeQuery();
      actorIds = new HashSet<String>();
      while (rs.next()) {
        actorIds.add(rs.getString(1));
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    if (name == null) {
      return null;
    }

    return new Movie(name, actorIds);
  }

  @Override
  public String getActorId(String name) {
    String query = "SELECT id FROM actor" + " WHERE lower(name) = lower(?);";

    PreparedStatement prep = null;
    ResultSet rs = null;
    String id = null;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, name);

      rs = prep.executeQuery();
      if (rs.next()) {
        id = rs.getString(1);
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    return id;
  }

  @Override
  public String getMovieId(String name) {
    String query = "SELECT id FROM film" + " WHERE lower(name) = lower(?);";

    PreparedStatement prep = null;
    ResultSet rs = null;
    String id = null;
    try {
      prep = conn.prepareStatement(query);
      prep.setString(1, name);

      rs = prep.executeQuery();
      if (rs.next()) {
        id = rs.getString(1);
      }
    } catch (SQLException e) {
      throw new RepositoryException(e.getMessage());
    } finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (prep != null) {
          prep.close();
        }
      } catch (SQLException e) {
        throw new RepositoryException(e.getMessage());
      }
    }

    return id;
  }

  @Override
  public void close() throws Exception {
    conn.close();
  }

}
