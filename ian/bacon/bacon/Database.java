package edu.brown.cs.is3.bacon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Database class for interfacing with SQL.
 * @author is3
 *
 */
public class Database {
  private final String urlToDB;
  private final Connection conn;

  /**
   * Constructs a db.
   * @param path to the database.
   * @throws ClassNotFoundException if class for name fails.
   * @throws SQLException on database error.
   */
  public Database(String path) throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    this.urlToDB = "jdbc:sqlite:" + path;
    this.conn = DriverManager.getConnection(urlToDB);
  }

  /**
   * Frees all resources associated with database.
   */
  public void close() {
    try {
      conn.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Builds an actor from an id.
   * @param id of actor to build.
   * @return the actor.
   */
  public Actor actorOfId(String id) {
    String actorQuery = "SELECT name FROM actor WHERE id = ? LIMIT 1";

    try (PreparedStatement actorPS = conn.prepareStatement(actorQuery)) {
      actorPS.setString(1, id);

      try (ResultSet actorRS = actorPS.executeQuery()) {
        String name;

        if (actorRS.next()) {
          name = actorRS.getString(1);

          if (actorRS.next()) {
            close();
            throw new RuntimeException("ERROR: Multiple actors with that id.");
          }
        } else {
          close();
          throw new RuntimeException("ERROR: No actor with that id.");
        }

        return new RealActor(id, name);
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Builds a film from an id.
   * @param id of film.
   * @return the film.
   */
  public Film filmOfId(String id) {
    String filmQuery = "SELECT name FROM film WHERE id = ? LIMIT 1;";

    try (PreparedStatement filmPS = conn.prepareStatement(filmQuery)) {
      filmPS.setString(1, id);

      try (ResultSet filmRS = filmPS.executeQuery()) {
        String name;

        if (filmRS.next()) {
          name = filmRS.getString(1);
          if (filmRS.next()) {
            close();
            throw new RuntimeException("ERROR: Multiple films with that id.");
          }
        } else {
          close();
          throw new RuntimeException("ERROR: No film with that id.");
        }

        return new RealFilm(id, name);
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Builds an actor from a name. Picks the one with the lowest id if several
   * share a name.
   * @param name of actor.
   * @return the actor.
   */
  public Actor actorOfName(String name) {
    String id;
    String actorQuery = "SELECT id FROM actor WHERE name = ? LIMIT 1;";

    try (PreparedStatement actorPS = conn.prepareStatement(actorQuery)) {
      actorPS.setString(1, name);

      try (ResultSet actorRS = actorPS.executeQuery()) {
        if (actorRS.next()) {
          id = actorRS.getString(1);
        } else {
          close();
          throw new RuntimeException("ERROR: No actor with that name.");
        }

        return new RealActor(id, name);
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Produces the set of films made by a given actor.
   * @param actor to be examined.
   * @return set of films starring that actor.
   */
  public Set<Film> filmsOfActor(Actor actor) {
    String filmQuery = "SELECT film FROM actor_film WHERE actor = ?;";
    Set<Film> toReturn = new HashSet<>();

    try (PreparedStatement filmPS = conn.prepareStatement(filmQuery)) {
      filmPS.setString(1, actor.getId());

      try (ResultSet filmRS = filmPS.executeQuery()) {
        while (filmRS.next()) {
          String filmID = filmRS.getString(1);
          toReturn.add(new FilmProxy(filmID));
        }
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }

    return toReturn;
  }

  /**
   * Produces all of the actors starring in a film.
   * @param film to be examined.
   * @return the list of stars.
   */
  public Set<Actor> starsOfFilm(Film film) {
    String actorQuery = "SELECT actor FROM actor_film WHERE film = ?;";
    Set<Actor> toReturn = new HashSet<>();

    try (PreparedStatement actorPS = conn.prepareStatement(actorQuery)) {
      actorPS.setString(1, film.getId());

      try (ResultSet actorRS = actorPS.executeQuery()) {
        while (actorRS.next()) {
          String actorId = actorRS.getString(1);
          toReturn.add(new ActorProxy(actorId));
        }
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }

    return toReturn;
  }

  public Set<String> allActorNames() {
    Set<String> toReturn = new HashSet<>();

    String query = "SELECT DISTINCT name FROM actor;";

    try (PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        toReturn.add(rs.getString(1));
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException("ERROR: Failed to generate full actor list.");
    }

    return toReturn;
  }
}
