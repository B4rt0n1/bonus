package com.mst;

import java.util.*;
import java.io.*;
import com.google.gson.*;

public class ReplacementFinder {

    public static void process(Graph graph, String outputPath) throws IOException {
        KruskalMST kruskal = new KruskalMST();
        List<Edge> mst = kruskal.buildMST(graph);
        System.out.println("Initial MST edges: " + mst);

        // Remove one random edge
        Random rand = new Random();
        Edge removed = mst.remove(rand.nextInt(mst.size()));
        System.out.println("Removed edge: " + removed);

        // Find components after removal
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (Edge e : mst) {
            adj.computeIfAbsent(e.src, k -> new ArrayList<>()).add(e.dest);
            adj.computeIfAbsent(e.dest, k -> new ArrayList<>()).add(e.src);
        }

        boolean[] visited = new boolean[graph.V];
        List<Set<Integer>> components = new ArrayList<>();

        for (int i = 0; i < graph.V; i++) {
            if (!visited[i]) {
                Set<Integer> comp = new HashSet<>();
                dfs(i, adj, visited, comp);
                components.add(comp);
            }
        }

        System.out.println("Components after removal: " + components);

        // Find minimum edge reconnecting two components
        Edge bestEdge = null;
        double minWeight = Double.MAX_VALUE;
        for (Edge e : graph.edges) {
            if (mst.contains(e)) continue;
            if (inDifferentComponents(e, components)) {
                if (e.weight < minWeight) {
                    minWeight = e.weight;
                    bestEdge = e;
                }
            }
        }

        if (bestEdge != null) {
            mst.add(bestEdge);
            System.out.println("Replacement edge added: " + bestEdge);
        }

        JsonObject result = new JsonObject();
        JsonArray before = new JsonArray();
        for (Edge e : mst) {
            JsonObject o = new JsonObject();
            o.addProperty("src", e.src);
            o.addProperty("dest", e.dest);
            o.addProperty("weight", e.weight);
            before.add(o);
        }
        result.add("mst_after", before);
        result.addProperty("removed_edge", removed.toString());
        result.addProperty("replacement_edge", bestEdge != null ? bestEdge.toString() : "none");

        try (Writer writer = new FileWriter(outputPath)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(result, writer);
        }
    }

    private static void dfs(int v, Map<Integer, List<Integer>> adj, boolean[] visited, Set<Integer> comp) {
        visited[v] = true;
        comp.add(v);
        if (!adj.containsKey(v)) return;
        for (int n : adj.get(v)) {
            if (!visited[n]) dfs(n, adj, visited, comp);
        }
    }

    private static boolean inDifferentComponents(Edge e, List<Set<Integer>> comps) {
        int srcComp = -1, destComp = -1;
        for (int i = 0; i < comps.size(); i++) {
            if (comps.get(i).contains(e.src)) srcComp = i;
            if (comps.get(i).contains(e.dest)) destComp = i;
        }
        return srcComp != -1 && destComp != -1 && srcComp != destComp;
    }
}
