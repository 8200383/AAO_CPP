package aao;


import java.util.Arrays;

public class Main {

    static public void main(String[] args) throws Exception {

        CPPSolver cpp = GraphBuilder.fromCSV(4, "example_1.csv");

        System.out.println(Arrays.deepToString(cpp.getArcs()));

        GraphPlotter.plot(cpp.getArcs(), "example_1_output.png");

        cpp.solve();
        cpp.printCPT(0);

        System.out.println(cpp.cost());
    }
}
