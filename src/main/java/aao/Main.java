package aao;

public class Main {

    static public void main(String[] args) {
        CPP G = new CPP(4); // create a graph of four vertices

        // add the arcs for the example graph
        G.addArc("a", 0, 1, 1);
        G.addArc("b", 0, 2, 1);
        G.addArc("c", 1, 2, 1);
        G.addArc("d", 1, 3, 1);
        G.addArc("e", 2, 3, 1);
        G.addArc("f", 3, 0, 1);

        G.solve(); // find the CPT
        G.printCPT(0); // print it, starting from vertex 0
        System.out.println("Cost = " + G.cost());
    }
}
