package aao;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class ChinesePostmanSolver {
    private static final int INFINITE = Integer.MAX_VALUE / 2;
    private final int[][] costMatrix;

    public ChinesePostmanSolver(int[][] costMatrix) {
        this.costMatrix = costMatrix;
    }

    public List<Integer> solve(int startVertex) {
        if (!this.checkSymmetry(this.costMatrix)) {
            throw new IllegalArgumentException("Cannot Solve Directed Graphs...");
        }

        System.out.println("Graph Order: " + this.costMatrix.length);

        // Find odd vertices
        LinkedList<Integer> oddVertices = this.findOddVertices(this.costMatrix);
        System.out.println("Odd Vertices: " + Arrays.toString(oddVertices.toArray()));

        // Run FloydWarshall
        Object[] results = this.performFloydWarshall(this.costMatrix);
        int[][] distance = (int[][]) results[0];
        int[][] next = (int[][]) results[1];
        LinkedList<LinkedList<Integer>> matchings = new LinkedList<>();

        // Find all matching of graph with oddVertices
        this.findAllMatchings(matchings, oddVertices, new LinkedList<>());

        //Finds match with minimum summed weight
        LinkedList<Integer> bestMatch = this.findPerfectMatch(matchings, distance);
        int[][][] multiGraph = this.addEdgesGraph(bestMatch, distance, this.costMatrix);

        LinkedList<Integer> circuit = this.performHierholzer(multiGraph, startVertex, next);
        System.out.println("Circuit: " + Arrays.toString(circuit.toArray()));

        int totalCost = this.getTotalCost(circuit, this.costMatrix);
        System.out.println("Total Cost: " + totalCost);

        return circuit;
    }

    /**
     * Find odd vertices from a cost matrix
     *
     * @param costMatrix the cost matrix
     * @return the list of odd vertices
     * @implNote Big O(n*n) = O(n^2) = O(|V|^2)
     */
    private LinkedList<Integer> findOddVertices(int[][] costMatrix) {

        int n = costMatrix.length;
        LinkedList<Integer> oddVertices = new LinkedList<>();

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
     * @param matchings empty list of matching vertices
     * @param vertices list of all vertices
     * @param visited list of visited vertices
     * @implNote Big O(n) = O(|V|)
     */

    private void findAllMatchings(LinkedList<LinkedList<Integer>> matchings,
                                  LinkedList<Integer> vertices,
                                  LinkedList<Integer> visited) {

        if (vertices.isEmpty()) {
            matchings.add(new LinkedList<>(visited));
            return;
        }
        if (vertices.size() % 2 == 0) {
            Integer visitVertex = vertices.getFirst();
            LinkedList<Integer> remainingVertices = new LinkedList<>(vertices);
            remainingVertices.remove(visitVertex);
            visited.add(visitVertex);
            findAllMatchings(matchings, remainingVertices, visited);
            visited.removeLast();

        } else if (vertices.size() % 2 != 0) {

            for (Integer visitVertex : vertices) {
                LinkedList<Integer> remainingVertices = new LinkedList<>(vertices);
                remainingVertices.remove(visitVertex);
                visited.add(visitVertex);
                findAllMatchings(matchings, remainingVertices, visited);
                visited.removeLast();

            }
        }

    }

    /**
     * Finds the perfect match with the minimum summed weight
     * @param matchings list of matching vertices
     * @param distance matrix of distances obtained in the Floyd-Warshall algorithm
     * @return list of best matching vertices
     * @implNote Big O(n*n) = O(n^2) = O(|V|^2)
     */

    private LinkedList<Integer> findPerfectMatch(LinkedList<LinkedList<Integer>> matchings, int[][] distance) {

        LinkedList<Integer> bestMatching = null;
        int bestCost = Integer.MAX_VALUE;

        for (LinkedList<Integer> match : matchings) {
            int cost = 0;
            for (int i = 0; i < match.size() - 1; i += 2) {
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
    private int[][][] addEdgesGraph(LinkedList<Integer> bestMatch, int[][] distance, int[][] costMatrix) {
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
    public boolean checkSymmetry(int[][] costMatrix) {
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
    private int getTotalCost(LinkedList<Integer> circuit, int[][] costMatrix) {
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

    //Floyd Warshall path reconstruction
    private LinkedList<Integer> getPath(int i, int j, int[][] next) {
        LinkedList<Integer> path = new LinkedList<>();
        while (i != j) {
            i = next[i][j];
            path.add(i);
        }
        return path;
    }

    public LinkedList<Integer> performHierholzer(int[][][] costMatrix, Integer start, int[][] nextMatrix) {

        int n = costMatrix.length;

        int[][][] visitedEdges = new int[n][n][2];
        LinkedList<Integer> circuit = new LinkedList<>();
        circuit.add(start);

        while (true) {
            Integer unvisited = -1;
            int index = 0;

            for (Integer vertice : circuit) {
                int[] nextEdge = findUnvisitedEdge(vertice, visitedEdges, costMatrix);
                if (nextEdge != null) {
                    unvisited = vertice;
                    break;
                }
                index += 1;
            }

            if (unvisited == -1) {
                break;
            }

            LinkedList<Integer> newCircle = new LinkedList<>();
            newCircle.add(unvisited);

            while (true) {
                int[] nextEdge = findUnvisitedEdge(unvisited, visitedEdges, costMatrix);

                if (nextEdge[2] == 0) {
                    newCircle.add(nextEdge[1]);

                } else if (nextEdge[2] == 1) {
                    newCircle.addAll(getPath(nextEdge[0], nextEdge[1], nextMatrix));

                }

                visitedEdges[nextEdge[0]][nextEdge[1]][nextEdge[2]] = 1;
                visitedEdges[nextEdge[1]][nextEdge[0]][nextEdge[2]] = 1;
                unvisited = nextEdge[1];

                if (newCircle.getFirst() == newCircle.getLast()) {
                    break;
                }
            }
            circuit.remove(index);
            circuit.addAll(index, newCircle);

        }

        return circuit;
    }

    /**
     * Perform Floyd Warshall Algorithm on a cost matrix
     *
     * @param costMatrix the cost matrix
     * @return an object with distance and next
     * @implNote Big O(n^2) = O(|V|^2)
     */
    public Object[] performFloydWarshall(int[][] costMatrix) {
        int n = costMatrix.length;
        int[][] distance = new int[n][n];
        int[][] next = new int[n][n];

        // Initialization O(n^2)
        for (int i = 0; i < n; i++) { // O(n)
            for (int j = 0; j < n; j++) { // O(n)
                distance[i][j] = costMatrix[i][j];
                if (costMatrix[i][j] != 0 && costMatrix[i][j] != INFINITE) {
                    next[i][j] = j;
                }
            }
        }

        // Algorithm Body O(n^3)
        for (int k = 0; k < n; k++) { // O(n)
            for (int i = 0; i < n; i++) { // O(n)
                for (int j = 0; j < n; j++) { // O(n)
                    if (distance[i][j] > distance[i][k] + distance[k][j]) {
                        distance[i][j] = distance[i][k] + distance[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }

        }

        return new Object[]{distance, next};
    }

}