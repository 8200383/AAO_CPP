package aao;


import java.io.IOException;
import java.util.Arrays;

public class Main {

    static public void main(String[] args) {
        System.out.println("-".repeat(50));
        runOne(4, 0, "example_1");
        System.out.println("-".repeat(50));
        runOne(4, 0, "example_2");
        System.out.println("-".repeat(50));
        runOne(9, 0, "example_3");
    }

    static public void runOne(int vertices, int startAt, String path) {
        CPPSolver cpp;

        try {
            cpp = GraphBuilder.fromCSV(vertices, path + ".csv");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println(Arrays.deepToString(cpp.getArcs()));

        try {
            GraphPlotter.plot(cpp.getArcs(), path + "_output.png");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        cpp.solve();
        cpp.printCPT(startAt);

        System.out.println(cpp.cost());
    }
}
