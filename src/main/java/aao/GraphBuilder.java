package aao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;

public class GraphBuilder {
    private static final String COMMA_DELIMITER = ",";

    public static CPPSolver fromCSV(int vertices, String path) throws Exception {
        URL resource = GraphBuilder.class.getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException(path + " (No such file or directory)");
        }

        File file = new File(resource.toURI());
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);

        CPPSolver cpp = new CPPSolver(vertices);

        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(COMMA_DELIMITER);
            System.out.println(Arrays.toString(values));

            if (values.length != 4) {
                throw new IllegalStateException(path + " (Has more or less then 4 columns! Accepted columns are: arc, u, v, cost)");
            }

            String arc = values[0];
            int u = Integer.parseInt(values[1]);
            int v = Integer.parseInt(values[2]);
            int cost = Integer.parseInt(values[3]);
            cpp.addArc(arc, u, v, cost);
        }

        return cpp;
    }
}
