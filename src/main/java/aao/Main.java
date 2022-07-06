package aao;

public class Main {

    static public void main(String[] args) {
        runOne(4, 0, "example_1");
        runOne(4, 0, "example_2");
        runOne(9, 0, "example_3");
    }

    static public void runOne(int vertices, int startAt, String path) {
        try {
            GraphBuilder.fromCSV(vertices, path + ".csv");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
