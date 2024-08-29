package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ShortestPathsTest {
    /** The graph example from Prof. Myers's notes. There are 7 vertices labeled a-g, as
     *  described by vertices1. 
     *  Edges are specified by edges1 as triples of the form {src, dest, weight}
     *  where src and dest are the indices of the source and destination
     *  vertices in vertices1. For example, there is an edge from a to d with
     *  weight 15.
     */
    static final String[] vertices1 = { "a", "b", "c", "d", "e", "f", "g" };
    static final int[][] edges1 = {
        {0, 1, 9}, {0, 2, 14}, {0, 3, 15},
        {1, 4, 23},
        {2, 4, 17}, {2, 3, 5}, {2, 5, 30},
        {3, 5, 20}, {3, 6, 37},
        {4, 5, 3}, {4, 6, 20},
        {5, 6, 16}
    };
    static class TestGraph implements WeightedDigraph<String, int[]> {
        int[][] edges;
        String[] vertices;
        Map<String, Set<int[]>> outgoing;

        TestGraph(String[] vertices, int[][] edges) {
            this.vertices = vertices;
            this.edges = edges;
            this.outgoing = new HashMap<>();
            for (String v : vertices) {
                outgoing.put(v, new HashSet<>());
            }
            for (int[] edge : edges) {
                outgoing.get(vertices[edge[0]]).add(edge);
            }
        }
        public Iterable<int[]> outgoingEdges(String vertex) { return outgoing.get(vertex); }
        public String source(int[] edge) { return vertices[edge[0]]; }
        public String dest(int[] edge) { return vertices[edge[1]]; }
        public double weight(int[] edge) { return edge[2]; }
    }
    static TestGraph testGraph1() {
        return new TestGraph(vertices1, edges1);
    }

    @Test
    void lectureNotesTest() {
        TestGraph graph = testGraph1();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(50, ssp.getDistance("g"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("g")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" g");
        assertEquals("best path: a c e f g", sb.toString());

        //test a to d
        StringBuilder sb2 = new StringBuilder();
        assertEquals(15, ssp.getDistance("d"));
        sb2.append("best path:");
        for (int[] e : ssp.bestPath("d")) {
            sb2.append(" " + vertices1[e[0]]);
        }
        sb2.append(" d");
        assertEquals("best path: a d", sb2.toString());

        //test loop between a, c, d
        ssp.singleSourceDistances("a");
        assertEquals(0, ssp.getDistance("a"));
        StringBuilder sb3 = new StringBuilder();
        sb3.append("best path:");
        for (int[] e : ssp.bestPath("a")) {
            sb3.append(" " + vertices1[e[0]]);
        }
        sb3.append(" a");
        assertEquals("best path: a", sb3.toString());
    }
}
