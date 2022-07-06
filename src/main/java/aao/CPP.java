package aao;

import java.util.Vector;

/**
 * Class for finding and printing the optimal Chinese Postman tour of multi-digraphs.
 * For more details, read <a href="http://www.uclic.ucl.ac.uk/harold/cpp">http://www.uclic.ucl.ac.uk/harold/cpp</a>.
 *
 * @author Harold Thimbleby, 2001, 2, 3
 */
@SuppressWarnings({"CStyleArrayDeclaration", "rawtypes", "ForLoopReplaceableByForEach", "StatementWithEmptyBody", "unchecked", "ManualArrayCopy", "ConstantConditions"})
public class CPP {
    private static final int NONE = -1; // anything < 0
    private final int N; // number of vertices
    private final int[] delta; // deltas of vertices
    private int neg[], pos[]; // unbalanced vertices
    private final int[][] arcs; // adjacency matrix, counts arcs between vertices
    private final Vector[][] label; // vectors of labels of arcs (for each vertex pair)
    private final int[][] f; // repeated arcs in CPT
    private final float[][] c; // costs of cheapest arcs or paths
    private final String[][] cheapestLabel; // labels of cheapest arcs
    private final boolean[][] defined; // whether path cost is defined between vertices
    private final int[][] path; // spanning tree of the graph
    private float basicCost; // total cost of traversing each arc once

    public void solve() {
        leastCostPaths();
        checkValid();
        findUnbalanced();
        findFeasible();
        while (improvements()) ;
    }

    public CPP(int vertices) {
        if ((N = vertices) <= 0) throw new Error("Graph is empty");
        delta = new int[N];
        defined = new boolean[N][N];
        label = new Vector[N][N];
        c = new float[N][N];
        f = new int[N][N];
        arcs = new int[N][N];
        cheapestLabel = new String[N][N];
        path = new int[N][N];
        basicCost = 0;
    }

    public float cost() {
        return basicCost + phi();
    }

    public void addArc(String lab, int u, int v, float cost) {
        if (!defined[u][v]) label[u][v] = new Vector();
        label[u][v].addElement(lab);
        basicCost += cost;
        if (!defined[u][v] || c[u][v] > cost) {
            c[u][v] = cost;
            cheapestLabel[u][v] = lab;
            defined[u][v] = true;
            path[u][v] = v;
        }
        arcs[u][v]++;
        delta[u]++;
        delta[v]--;
    }

    /**
     * Floyd-Warshall algorithm
     * Assumes no negative self-cycles.
     * Finds least cost paths or terminates on finding any non-trivial negative cycle.
     */
    private void leastCostPaths() {
        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                if (defined[i][k]) {
                    for (int j = 0; j < N; j++) {
                        if (defined[k][j] && (!defined[i][j] || c[i][j] > c[i][k] + c[k][j])) {
                            path[i][j] = path[i][k];
                            c[i][j] = c[i][k] + c[k][j];
                            defined[i][j] = true;
                            if (i == j && c[i][j] < 0) return; // stop on negative cycle
                        }
                    }
                }
            }
        }
    }

    private void checkValid() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                if (!defined[i][j]) throw new Error("Graph is not strongly connected");
            if (c[i][i] < 0) throw new Error("Graph has a negative cycle");
        }
    }

    private float phi() {
        float phi = 0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                phi += c[i][j] * f[i][j];
        return phi;
    }

    private void findUnbalanced() {
        int nn = 0, np = 0; // number of vertices of negative/positive delta

        for (int i = 0; i < N; i++)
            if (delta[i] < 0) nn++;
            else if (delta[i] > 0) np++;

        neg = new int[nn];
        pos = new int[np];
        nn = np = 0;
        for (int i = 0; i < N; i++) // initialise sets
            if (delta[i] < 0) neg[nn++] = i;
            else if (delta[i] > 0) pos[np++] = i;
    }

    private void findFeasible() {    // delete next 3 lines to be faster, but non-reentrant
        int delta[] = new int[N];
        for (int i = 0; i < N; i++)
            delta[i] = this.delta[i];

        for (int u = 0; u < neg.length; u++) {
            int i = neg[u];
            for (int v = 0; v < pos.length; v++) {
                int j = pos[v];
                f[i][j] = Math.min(-delta[i], delta[j]);
                delta[i] += f[i][j];
                delta[j] -= f[i][j];
            }
        }
    }

    private boolean improvements() {
        CPP residual = new CPP(N);
        for (int u = 0; u < neg.length; u++) {
            int i = neg[u];
            for (int v = 0; v < pos.length; v++) {
                int j = pos[v];
                residual.addArc(null, i, j, c[i][j]);
                if (f[i][j] != 0) residual.addArc(null, j, i, -c[i][j]);
            }
        }
        residual.leastCostPaths(); // find a negative cycle
        for (int i = 0; i < N; i++)
            if (residual.c[i][i] < 0) // cancel the cycle (if any)
            {
                int k = 0, u, v;
                boolean kunset = true;
                u = i;
                do // find k to cancel
                {
                    v = residual.path[u][i];
                    if (residual.c[u][v] < 0 && (kunset || k > f[v][u])) {
                        k = f[v][u];
                        kunset = false;
                    }
                } while ((u = v) != i);
                u = i;
                do // cancel k along the cycle
                {
                    v = residual.path[u][i];
                    if (residual.c[u][v] < 0) f[v][u] -= k;
                    else f[u][v] += k;
                } while ((u = v) != i);
                return true; // have another go
            }
        return false; // no improvements found
    }

    private int findPath(int from, int f[][]) // find a path between unbalanced vertices
    {
        for (int i = 0; i < N; i++)
            if (f[from][i] > 0) return i;
        return NONE;
    }

    public void printCPT(int startVertex) {
        int v = startVertex;

        // delete next 7 lines to be faster, but non-reentrant
        int arcs[][] = new int[N][N];
        int f[][] = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                arcs[i][j] = this.arcs[i][j];
                f[i][j] = this.f[i][j];
            }
        }

        while (true) {
            int u = v;
            if ((v = findPath(u, f)) != NONE) {
                f[u][v]--; // remove path
                for (int p; u != v; u = p) // break down path into its arcs
                {
                    p = path[u][v];
                    System.out.println("Take arc " + cheapestLabel[u][p]
                            + " from " + u + " to " + p);
                }
            } else {
                int bridgeVertex = path[u][startVertex];
                if (arcs[u][bridgeVertex] == 0)
                    break; // finished if bridge already used
                v = bridgeVertex;
                for (int i = 0; i < N; i++) // find an unused arc, using bridge last
                    if (i != bridgeVertex && arcs[u][i] > 0) {
                        v = i;
                        break;
                    }
                arcs[u][v]--; // decrement count of parallel arcs
                System.out.println("Take arc " + label[u][v].elementAt(arcs[u][v])
                        + " from " + u + " to " + v); // use each arc label in turn
            }
        }
    }

}
