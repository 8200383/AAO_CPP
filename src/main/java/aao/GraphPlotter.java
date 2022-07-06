package aao;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.IOException;

public class GraphPlotter {

    public static void plot(int[][] matrix, String outputPath) throws IOException {
        StringBuilder dot = new StringBuilder("graph{");

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == 1) {
                    dot.append(j).append("--").append(i).append(';');
                }
            }
        }

        dot.append('}');

        Graphviz.useEngine(new GraphvizCmdLineEngine());
        MutableGraph g = new Parser().read(dot.toString());
        Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File(outputPath));
    }
}
