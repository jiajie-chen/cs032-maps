package edu.brown.cs.jc124.traffic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TrafficManager implements Runnable {
  private long unixTime;
  private final int serverPort;
  private final Map<String, Double> trafficById;
  private static final String BASE_URL = "http://localhost";
  private static final long MS_DELAY = 250;

  public TrafficManager(int serverPort) {
    this.serverPort = serverPort;
    this.unixTime = 0;
    this.trafficById = new HashMap<>();

    try {
      URL query = new URL(BASE_URL + ":" + serverPort + "?last=" + unixTime);
      URLConnection conn = query.openConnection();

      try (BufferedReader r = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), "UTF-8"))) {

        String s = r.readLine();
        while (s != null) {
          System.out.println(s);
          s = r.readLine();
          // TODO
        }

      }
    } catch (IOException e) {
      System.err.println("ERROR: Traffic setup failed with " + e.getMessage());
      return;
    }
  }

  @Override
  public void run() {

    while (true) {
      try {
        TimeUnit.MILLISECONDS.sleep(MS_DELAY);
      } catch (InterruptedException e) {
        System.err.println("ERROR: Woken up early with " + e.getMessage());
      }

      try {
        URL query = new URL(BASE_URL + ":" + serverPort + "?last=" + unixTime);
        URLConnection conn = query.openConnection();

        try (BufferedReader r = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), "UTF-8"))) {

          String s = r.readLine();
          while (s != null) {
            System.out.println(s);
            s = r.readLine();
            // TODO
          }
        }
      } catch (IOException e) {
        System.err
            .println("ERROR: Traffic parsing failed with " + e.getMessage());
        return;
      }
    }

  }
}
