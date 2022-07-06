package aao;

public class Main {

    static public void main(String[] args) {
        runOne(4, 0, "example_1");
        runOne(4, 0, "example_2");
        runOne(9, 0, "example_3");
    }

    static public void runOne(int vertices, int startAt, String path) {
        try {
            int[][] matrix = GraphBuilder.fromCSV(path + ".csv", vertices);

            GraphPlotter.plot(matrix, path + "_output");

            new ChinesePostmanSolver(matrix).solve(startAt);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
