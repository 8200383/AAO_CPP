package aao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;

public class GraphBuilder {
    private static final String COMMA_DELIMITER = ",";
    private static final int NUM_COLUMNS = 3;

    public static int[][] fromCSV(String path, int vertices) throws Exception {
        URL resource = GraphBuilder.class.getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException(path + " (No such file or directory)");
        }

        File file = new File(resource.toURI());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();

            String[] values = line.split(COMMA_DELIMITER);
            if (values.length != NUM_COLUMNS) {
                throw new IllegalStateException(path + " (Has more or less then 3 columns! Accepted columns are: u, v, cost)");
            }
        }

        int[][] matrix = new int[vertices][vertices];

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Integer[] values = Arrays.stream(line.trim().split(COMMA_DELIMITER))
                        .map(Integer::parseUnsignedInt)
                        .toArray(Integer[]::new);

                System.out.println(Arrays.toString(values));

                int u = values[0];
                int v = values[1];
                int cost = values[2];
                matrix[u][v] = cost;
                matrix[v][u] = cost;
            }
        }

        return matrix;
    }
}
