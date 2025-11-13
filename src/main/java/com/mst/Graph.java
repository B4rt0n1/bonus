package com.mst;

import java.io.*;
import java.util.*;
import com.google.gson.*;

public class Graph {
    int V;
    List<Edge> edges = new ArrayList<>();

    public static Graph fromJson(String path) throws IOException {
        Reader reader = new FileReader(path);
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
        int vertices = json.get("vertices").getAsInt();
        Graph g = new Graph(vertices);
        JsonArray eArray = json.getAsJsonArray("edges");
        for (JsonElement e : eArray) {
            JsonObject obj = e.getAsJsonObject();
            g.addEdge(obj.get("src").getAsInt(), obj.get("dest").getAsInt(), obj.get("weight").getAsDouble());
        }
        return g;
    }

    public Graph(int V) {
        this.V = V;
    }

    public void addEdge(int src, int dest, double weight) {
        edges.add(new Edge(src, dest, weight));
    }
}
