package aao;


public class Main {

    static public void main(String[] args) throws Exception {

        CPP cpp = GraphBuilder.fromCSV(4, "example_1.csv");

        cpp.solve();
        cpp.printCPT(0);

        System.out.println(cpp.cost());
    }
}
