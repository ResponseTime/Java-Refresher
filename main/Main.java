package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;

import com.sun.net.httpserver.*;

public class Main {
  public static void main(String[] args) {
    try {
      HttpServer hs = HttpServer.create(new InetSocketAddress(3000), 0);
      hs.createContext("/get-video-stream", new HttpHandler() {
        public void handle(HttpExchange exchange) throws IOException {
          File fs = new File("./vid.mp4");
          exchange.getResponseHeaders().set("Content-Type", "video/mp4");
          exchange.sendResponseHeaders(200, fs.length());

          try (OutputStream os = exchange.getResponseBody();
              BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fs))) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
              os.write(buffer, 0, bytesRead);
            }
          }
        }
      });
      hs.createContext("/get-image", new HttpHandler() {
        public void handle(HttpExchange exchange) throws IOException {
          File fs = new File("./wall.png");
          byte[] bytes = Files.readAllBytes(fs.toPath());
          exchange.getResponseHeaders().set("Content-Type", "image/png");
          exchange.sendResponseHeaders(200, bytes.length);
          try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
          }
        }
      });
      hs.setExecutor(null);
      hs.start();
    } catch (Exception e) {
      System.err.println(e);
    }
    System.out.println("HEllo wrodl");
    ArrayList<Integer> array = new ArrayList<>(List.of(1, 4, 6, 78, 9, 6));

    List<Integer> ti = array.stream()
        .filter(x -> x % 2 == 0)
        .map(n -> n ^ 2)
        .collect(Collectors.toList());
    System.out.println(ti.toString());
    Refresher ref = new Refresher();
    System.out.println(ref.isPalindrome("abaa"));
    ref.subSets(new ArrayList<Integer>(List.of(1, 2, 3)), new ArrayList<>(), 0);
    String[] links = new String[] {
        "https://en.wikipedia.org/wiki/Main_Page",
        "https://en.wikipedia.org/wiki/List_of_programming_languages",
        "https://en.wikipedia.org/wiki/List_of_Internet_top-level_domains",
        "https://github.com/topics",
        "https://github.com/search?q=java",
        "https://www.w3schools.com/html/html_links.asp",
        "https://www.w3schools.com/html/html_examples.asp",
        "https://news.ycombinator.com/",
        "https://old.reddit.com/r/programming/",
        "https://old.reddit.com/r/java/",
        "https://www.imdb.com/chart/top",
        "https://www.imdb.com/search/title/?genres=action",
        "https://www.bbc.com/news",
        "https://www.bbc.com/news/world",
        "https://stackoverflow.com/tags",
        "https://stackoverflow.com/questions/tagged/java",
        "http://dmoztools.net/Computers/Programming/Languages/Java/",
        "https://www.gutenberg.org/browse/scores/top"
    };
    for (String url : links) {
      Thread t = new Thread(new Fetch(url));
      t.start();
    }
  }
}

class Fetch implements Runnable {
  private String url;
  static int Counter;

  Fetch(String url) {
    this.url = url;
  }

  public void run() {
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(this.url))
          .GET()
          .build();
      HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
      Counter++;
      System.out.println(res.statusCode() + " " + url);
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}

class Refresher {
  Refresher() {
    System.out.println("Refresher starts");
  }

  public boolean isPalindrome(String s) {
    if (s.length() == 1) {
      return true;
    }
    if (s.charAt(0) != s.charAt(s.length() - 1)) {
      return false;
    }
    return isPalindrome(s.substring(1, s.length() - 1));
  }

  public void subSets(ArrayList<Integer> ar, ArrayList<Integer> tmp, int ind) {
    if (ind == ar.size()) {
      System.out.println(tmp.clone());
      return;
    }
    tmp.add(ar.get(ind));
    subSets(ar, tmp, ind + 1);
    tmp.remove(tmp.size() - 1);
    subSets(ar, tmp, ind + 1);
  }
}