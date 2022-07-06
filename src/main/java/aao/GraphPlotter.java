package aao;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.IOException;

public class GraphPlotter {
    private final int width;
    private final Format format;
    private final Parser parser;

    public GraphPlotter(int width, Format format) {
        this.width = width;
        this.format = format;

        Graphviz.useEngine(new GraphvizCmdLineEngine());
        this.parser = new Parser();
    }

    private void render(String dotLang, File outputFile, boolean directed) throws IOException {
        MutableGraph graph = this.parser.read(dotLang);
        graph.setDirected(directed);

        Graphviz.fromGraph(graph)
                .width(this.width)
                .render(this.format)
                .toFile(outputFile);

    }

    public void plotMatrix(int[][] matrix, String outputPath) throws IOException {
        StringBuilder dot = new StringBuilder("graph{");

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == 1) {
                    dot.append(j).append("--").append(i).append(';');
                }
            }
        }

        dot.append('}');

        render(dot.toString(), new File(outputPath), false);
    }

    public void plotArray(int[] arr, String outputPath) throws IOException {
        StringBuilder dot = new StringBuilder("graph{");

        for (int i = 0; i < arr.length - 1; i++) {
            dot.append(arr[i]).append("--").append(arr[i + 1]).append(';');
        }

        dot.append('}');

        render(dot.toString(), new File(outputPath), true);
    }
}
