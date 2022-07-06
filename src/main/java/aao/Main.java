package aao;

import guru.nidi.graphviz.engine.Format;

import java.util.List;

public class Main {

    static public void main(String[] args) {
        runOne(4, 0, "example_1");
        runOne(4, 0, "example_2");
        runOne(9, 0, "example_3");
    }

    static public void runOne(int vertices, int startAt, String path) {
        GraphPlotter plotter = new GraphPlotter(500, Format.PNG);

        System.out.println("-".repeat(50));

        try {
            int[][] matrix = GraphBuilder.fromCSV(path + ".csv", vertices);

            plotter.plotMatrix(matrix, path + "_output");

            List<Integer> circuit = new ChinesePostmanSolver(matrix).solve(startAt);

            int[] c = circuit.stream().mapToInt(Integer::intValue).toArray();
            plotter.plotArray(c, path + "_circuit");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
