package edu.brown.cs.is3.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import edu.brown.cs.is3.bacon.Actor;
import edu.brown.cs.is3.bacon.ActorEdge;
import edu.brown.cs.is3.bacon.ActorVertex;
import edu.brown.cs.is3.bacon.Film;

/**
 * Class for efficiently performing dijkstras searches on actors from a
 * database.
 * @author is3
 *
 */
public class ActorSearch {
  private Map<String, ActorVertex> actorVertexById = new HashMap<>();

  /**
   * Searches beginning at an actor vertex until it reaches the ending vertex.
   * @param start actor to start at.
   * @param end actor to end at.
   * @param check ConnectionChecker to determine if connection is valid.
   * @return a list of the edges in the shortest path from start to end.
   */
  public List<ActorEdge> dijkstras(ActorVertex start, ActorVertex end,
      FirstLastChecker check) {

    PriorityQueue<ActorVertex> pq = new PriorityQueue<>();
    Map<ActorVertex, Double> distances = new HashMap<>();
    Set<ActorVertex> explored = new HashSet<>();

    start.setWeight(0);
    end.setWeight(Double.MAX_VALUE);

    actorVertexById.put(start.getElement().getId(), start);
    actorVertexById.put(end.getElement().getId(), end);

    ActorVertex curr = start;
    pq.add(curr);
    distances.put(curr, curr.getWeight());

    while (!pq.isEmpty()) {
      curr = pq.poll();

      if (curr.equals(end)) {
        return generateSolution(curr);
      }

      explored.add(curr);

      for (Edge<Actor> ae : getEdges(curr, check)) {
        ActorVertex next = (ActorVertex) ae.getTarget();
        if (!check.validConnection(ae)) {
          continue;
        }

        if (!explored.contains(next)) {
          if (!pq.contains(next)) {
            next.setPrev(curr);
            next.setPrevEdge(ae);
            next.setWeight(curr.getWeight() + ae.getWeight());
            pq.add((ActorVertex) next);
            actorVertexById.put(next.getElement().getId(), next);
            distances.put(next, next.getWeight());
          } else if (distances.getOrDefault(curr, Double.MAX_VALUE) > curr
              .getWeight() + ae.getWeight()) {

            pq.remove(next);
            next.setPrev(curr);
            next.setPrevEdge(ae);
            next.setWeight(curr.getWeight() + ae.getWeight());
            pq.add(next);
            actorVertexById.put(next.getElement().getId(), next);
            distances.put(next, next.getWeight());
          }
        }
      }
    }

    return new ArrayList<>();
  }

  /**
   * Produces a solution for dijkstras as above by tracing back from the end
   * node to the start.
   * @param curr node to begin tracing back from.
   * @return the edges collecting curr to the start.
   */
  private List<ActorEdge> generateSolution(ActorVertex curr) {
    List<ActorEdge> toReturn = new ArrayList<>();
    if (curr == null || curr.getPrev() == null) {
      return toReturn;
    }

    toReturn.add(0, (ActorEdge) curr.getPrevEdge());
    while (curr.getPrev() != null && curr.getPrev().getPrevEdge() != null) {
      curr = (ActorVertex) curr.getPrev();
      toReturn.add(0, (ActorEdge) curr.getPrevEdge());
    }

    return toReturn;
  }

  /**
   * Generates and returns the edges connecting a vertex.
   * @param av actor vertex to examine.
   * @param check a connection checker.
   * @return edges emanating from a vertex as set.
   */
  public Set<Edge<Actor>> getEdges(ActorVertex av,
      ConnectionChecker<Actor> check) {

    if (!av.getEdges().isEmpty()) {
      return av.getEdges();
    }

    for (Film f : av.getElement().getFilms()) {
      Set<Actor> stars = f.getStars();
      double edgeWeight = 1.0 / stars.size();
      for (Actor a : stars) {
        ActorVertex vert = actorVertexById.getOrDefault(a.getId(),
            new ActorVertex(a, Double.MAX_VALUE));

        actorVertexById.put(vert.getElement().getId(), vert);
        Edge<Actor> forward = new ActorEdge(av, vert, edgeWeight, f);

        av.addEdge(forward);
      }
    }

    actorVertexById.put(av.getElement().getId(), av);
    return av.getEdges();
  }
}
