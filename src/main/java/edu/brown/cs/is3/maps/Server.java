package edu.brown.cs.is3.maps;

/**
 * Server class hosting spark server.
 * @author is3
 *
 */
public class Server implements Runnable {
  private final Main m;
  private final int port;

  public Server(Main m, int port) {
    this.m = m;
    this.port = port;
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub

  }
}
