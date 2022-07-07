package aao;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class ChinesePostmanSolver {
    private static final int INFINITE = Integer.MAX_VALUE / 2;
    private final int[][] costMatrix;
    private final int[][] distance;

    public ChinesePostmanSolver(int[][] costMatrix) {
        this.costMatrix = costMatrix;
        this.distance = new int[costMatrix.length][costMatrix.length];
    }

    /**
     * Solve the Chinese Postman Problem
     *
     * @param startVertex the vertex to start the circuit
     * @return the circuit
     * @implNote Big O(n^3)
     */
    public List<Integer> solve(int startVertex) {
        System.out.println("Eulerian: " + this.isEulerian(this.costMatrix));

        if (!this.isConnected(this.costMatrix)) {
            throw new IllegalStateException("Cannot Solve Directed Graphs...");
        }

        System.out.println("Graph Order: " + this.costMatrix.length);

        // Find odd vertices
        List<Integer> oddVertices = this.findOddVertices(this.costMatrix); // O(n^2)
        System.out.println("Odd Vertices: " + Arrays.toString(oddVertices.toArray()));

        // Run FloydWarshall
        int[][] shortestPath = this.performFloydWarshall(this.costMatrix); // O(n^3)

        // Find all matching of graph with oddVertices
        List<List<Integer>> matchings = new LinkedList<>();
        LinkedList<Integer> visited = new LinkedList<>();
        this.findAllMatchings(matchings, (LinkedList<Integer>) oddVertices, visited); // O(n^2)

        // Finds match with minimum summed weight
        List<Integer> bestMatch = this.findPerfectMatch(matchings, this.distance);  // O(n^2)
        int[][][] multiGraph = this.addEdgesGraph(bestMatch, this.distance, this.costMatrix);  // O(n^2)

        List<Integer> circuit = this.performHierholzer(multiGraph, startVertex, shortestPath); // O(n^3)
        System.out.println("Circuit: " + Arrays.toString(circuit.toArray()));

        int totalCost = this.getTotalCost(circuit, this.costMatrix); // O(n^2)
        System.out.println("Total Cost: " + totalCost);

        return circuit;
    }

    /**
     * Check if is eulerian
     *
     * @param costMatrix the cost matrix
     * @return true if it does not have odd vertices
     */
    public boolean isEulerian(int[][] costMatrix) {
        int cc = costMatrix.length;
        if (cc == 0) return false;
        if (cc < 2)
            return true;

        if (!this.isConnected(costMatrix)) // O(n^2)
            return false;

        List<Integer> oddVertices = this.findOddVertices(this.costMatrix);  // O(n^2)

        return oddVertices.isEmpty();
    }

    /**
     * Find odd vertices from a cost matrix
     *
     * @param costMatrix the cost matrix
     * @return the list of odd vertices
     * @implNote Big O(n*n) = O(n^2) = O(|V|^2)
     */
    private List<Integer> findOddVertices(int[][] costMatrix) {

        int n = costMatrix.length;
        List<Integer> oddVertices = new LinkedList<>();

        for (int i = 0; i < n; i++) { // O(n)
            int neighborsCount = 0;
            for (int j = 0; j < n; j++) { // O(n)
                if (costMatrix[i][j] != INFINITE && costMatrix[i][j] != 0) {
                    neighborsCount += 1;
                }
            }
            if (neighborsCount % 2 != 0) {
                oddVertices.add(i);
            }
        }

        return oddVertices;
    }

    /**
     * Finds all matching vertices on the graph with oddVertices
     *
     * @param matchings   empty list of matching vertices
     * @param oddVertices list of odd vertices
     * @implNote Big O(n^2) = O(|V|^2)
     */
    private void findAllMatchings(List<List<Integer>> matchings,
                                  LinkedList<Integer> oddVertices,
                                  LinkedList<Integer> visited) {

        if (oddVertices.isEmpty()) {
            matchings.add(new LinkedList<>(visited));
            return;
        }

        if (oddVertices.size() % 2 == 0) {
            Integer visitVertex = oddVertices.getFirst(); // O(1)
            LinkedList<Integer> remainingVertices = new LinkedList<>(oddVertices);
            remainingVertices.remove(visitVertex); // O(n)
            visited.add(visitVertex); // O(1)
            this.findAllMatchings(matchings, remainingVertices, visited);
            visited.removeLast(); // O(1)
        }

        // O(n*n) = O(n^2)
        for (Integer visitVertex : oddVertices) { // O(n)
            LinkedList<Integer> remainingVertices = new LinkedList<>(oddVertices);
            remainingVertices.remove(visitVertex); // O(n)

            visited.add(visitVertex); // O(1)
            this.findAllMatchings(matchings, remainingVertices, visited);
            visited.removeLast(); // O(1)
        }
    }

    /**
     * Finds the perfect match with the minimum summed weight
     *
     * @param matchings list of matching vertices
     * @param distance  matrix of distances obtained in the Floyd-Warshall algorithm
     * @return list of best matching vertices
     * @implNote Big O(n*n) = O(n^2) = O(|V|^2)
     */
    private List<Integer> findPerfectMatch(List<List<Integer>> matchings, int[][] distance) {

        List<Integer> bestMatching = null;
        int bestCost = Integer.MAX_VALUE;

        for (List<Integer> match : matchings) { // O(n)
            int cost = 0;
            for (int i = 0; i < match.size() - 1; i += 2) { // O(n)
                cost += distance[match.get(i)][match.get(i + 1)];
            }

            if (cost < bestCost) {
                bestCost = cost;
                bestMatching = match;
            }

        }

        return bestMatching;
    }

    /**
     * Builds a multi graph
     *
     * @param bestMatch  the best match list
     * @param distance   the distance matrix
     * @param costMatrix the cost matrix
     * @return the multi graph
     * @implNote Big O(n^2) = O(|V|^2)
     */
    private int[][][] addEdgesGraph(List<Integer> bestMatch, int[][] distance, int[][] costMatrix) {
        int n = costMatrix.length;
        int[][][] multiGraph = new int[n][n][2]; // Maximum 2 edges by pair (i,j)

        for (int i = 0; i < n; i++) { // O(n)
            for (int j = 0; j < n; j++) {  // O(n)
                multiGraph[i][j][0] = costMatrix[i][j];
                multiGraph[i][j][1] = (i == j) ? 0 : INFINITE;
            }
        }

        for (int i = 0; i < bestMatch.size(); i += 2) { // O(n)
            multiGraph[bestMatch.get(i)][bestMatch.get(i + 1)][1] = distance[bestMatch.get(i)][bestMatch.get(i + 1)];
            multiGraph[bestMatch.get(i + 1)][bestMatch.get(i)][1] = distance[bestMatch.get(i)][bestMatch.get(i + 1)];
        }

        return multiGraph;
    }

    /**
     * Check the symmetry of a graph
     *
     * @param costMatrix the cost matrix
     * @return true if the graph is symmetric and false if the graph is not symmetric
     * @implNote Big O(n^2) = O(|V|^2)
     */
    public boolean isConnected(int[][] costMatrix) {
        int n = costMatrix.length;

        // O(n^2)
        for (int i = 0; i < n; i++) { // O(n)
            for (int j = 0; j < n; j++) { // O(n)
                if (costMatrix[i][j] != costMatrix[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get the total cost for a circuit
     *
     * @param circuit    the generated circuit
     * @param costMatrix the cost matrix
     * @return the total cost for a circuit
     * @implNote Big O(n^2) = O(|V|^2)
     */
    private int getTotalCost(List<Integer> circuit, int[][] costMatrix) {
        int totalCost = 0;

        //O(n^2)
        for (int i = 0; i < circuit.size() - 1; i++) { //O(n)
            int u = circuit.get(i); //O(n)
            int v = circuit.get(i + 1); //O(n)

            totalCost += costMatrix[u][v]; //O(1)
        }

        return totalCost;
    }


    /**
     * Find unvisited edge
     *
     * @param vertex       the last visited vertex
     * @param visitedEdges a 3D array of the visited edges
     * @param costMatrix   the cost matrix
     * @return the unvisited edge if it exists
     * @implNote Big O(n^2) = O(|V|^2)
     */
    private int[] findUnvisitedEdge(int vertex, int[][][] visitedEdges, int[][][] costMatrix) {

        //O(n^2)
        for (int j = 0; j < costMatrix.length; j++) { //O(n)
            for (int k = 0; k < 2; k++) { //O(n)
                int cost = costMatrix[vertex][j][k];
                if ((cost != INFINITE) && (cost != 0)
                        && (visitedEdges[vertex][j][k] != 1)) {

                    return new int[]{vertex, j, k};
                }
            }
        }
        return null;
    }

    /**
     * Floyd Warshall path reconstruction
     *
     * @param from the vertex from
     * @param to   the vertex to
     * @param next the next sub matrix
     * @return the reconstruction of the path
     * @implNote Big O(n)
     */
    private LinkedList<Integer> getFloydWarshallPath(int from, int to, int[][] next) {
        LinkedList<Integer> path = new LinkedList<>();

        while (from != to) { // O(n)
            from = next[from][to]; // O(1)
            path.add(from); // O(1)
        }

        return path;
    }

    /**
     * Perform Hierholzer on a cost matrix
     *
     * @param costMatrix  the cost matrix
     * @param startVertex the start vertex
     * @param nextMatrix  the sub matrix
     * @return the circuit
     * @implNote Big O(n^3)
     */
    public List<Integer> performHierholzer(int[][][] costMatrix, int startVertex, int[][] nextMatrix) {

        int n = costMatrix.length;

        int[][][] visitedEdges = new int[n][n][2];
        List<Integer> circuit = new LinkedList<>();
        circuit.add(startVertex); // O(1)

        while (true) { // O(n^3)
            int unvisited = -1;
            int index = 0;

            // O(n) * O(n^2) = O(n*n^2) = O(n^3)
            for (Integer vertex : circuit) { // O(n)
                int[] nextEdge = this.findUnvisitedEdge(vertex, visitedEdges, costMatrix); // O(n^2)
                if (nextEdge != null) {
                    unvisited = vertex;
                    break;
                }
                index += 1;
            }

            // Acontece quando não existe circuito Best O(1) | Worst O(circuit.length) = O(n)
            if (unvisited == -1) { // while true depends on unvisited = -1
                break;
            }

            LinkedList<Integer> newCircle = new LinkedList<>();
            newCircle.add(unvisited); // O(1)


            while (true) { // Worst Case O(n^2)
                int[] nextEdge = this.findUnvisitedEdge(unvisited, visitedEdges, costMatrix); // O(n^2)

                if (nextEdge[2] == 0)
                    newCircle.add(nextEdge[1]); // O(1)

                else if (nextEdge[2] == 1) {
                    LinkedList<Integer> path = this.getFloydWarshallPath(nextEdge[0], nextEdge[1], nextMatrix); // O(n)
                    newCircle.addAll(path); // O(n)
                }

                visitedEdges[nextEdge[0]][nextEdge[1]][nextEdge[2]] = 1;
                visitedEdges[nextEdge[1]][nextEdge[0]][nextEdge[2]] = 1;
                unvisited = nextEdge[1];

                if (newCircle.getFirst() == newCircle.getLast()) { // O(1)
                    break;
                }
            }

            circuit.remove(index); // O(n)
            circuit.addAll(index, newCircle); // O(n)
        }

        return circuit;
    }

    /**
     * Perform Floyd Warshall Algorithm on a cost matrix
     *
     * @param costMatrix the cost matrix
     * @implNote Big O(n^3) = O(|V|^3)
     */
    public int[][] performFloydWarshall(int[][] costMatrix) {
        int n = costMatrix.length;
        int[][] shortestPath = new int[n][n];

        // Initialization O(n^2)
        for (int i = 0; i < n; i++) { // O(n)
            for (int j = 0; j < n; j++) { // O(n)
                this.distance[i][j] = costMatrix[i][j];
                if (costMatrix[i][j] != 0 && costMatrix[i][j] != INFINITE) {
                    shortestPath[i][j] = j;
                }
            }
        }

        // Algorithm Body O(n^3)
        for (int k = 0; k < n; k++) { // O(n)
            for (int i = 0; i < n; i++) { // O(n)
                for (int j = 0; j < n; j++) { // O(n)
                    if (this.distance[i][j] > this.distance[i][k] + this.distance[k][j]) {
                        this.distance[i][j] = this.distance[i][k] + this.distance[k][j];
                        shortestPath[i][j] = shortestPath[i][k];
                    }
                }
            }
        }

        return shortestPath;
    }

}