package com.mst;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: mvn exec:java -Dexec.args=\\\"path_to_json\\\"");
            return;
        }
        String inputPath = args[0];
        String fileName = new File(inputPath).getName().replace(".json", "");
        String outputPath = "results/" + fileName + "_results.json";

        new File("results").mkdirs();

        Graph g = Graph.fromJson(inputPath);
        ReplacementFinder.process(g, outputPath);

        System.out.println("Results saved to: " + outputPath);
    }
}
