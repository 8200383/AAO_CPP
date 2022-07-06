package aao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;

public class GraphBuilder {
    private static final String COMMA_DELIMITER = ",";

    public static int[][] fromCSV(int vertices, String path) throws Exception {
        URL resource = GraphBuilder.class.getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException(path + " (No such file or directory)");
        }

        File file = new File(resource.toURI());
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);

        int lineCount;
        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            lineCount = (int) stream.count();
        }

        int[][] matrix = new int[lineCount][3];

        String line;
        int index = 0;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(COMMA_DELIMITER);
            System.out.println(Arrays.toString(values));

            if (values.length != 3) {
                throw new IllegalStateException(path + " (Has more or less then 4 columns! Accepted columns are: arc, u, v, cost)");
            }

            matrix[index][0] = Integer.parseInt(values[0]);
            matrix[index][1] = Integer.parseInt(values[1]);
            matrix[index][2] = Integer.parseInt(values[2]);

            index++;
        }
        System.out.println("----------- MATRIX ------------");
        System.out.println(Arrays.deepToString(matrix));
        System.out.println("-------------------------------");
        return matrix;
    }
}
