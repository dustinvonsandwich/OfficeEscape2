/*
University of Washington, Tacoma
TCSS 360 Software Development and Quality Assurance Techniques

Instructor: Tom Capaul
Academic Quarter: Summer 2021
Assignment: Group Project
Team members: Dustin Ray, Raz Consta, Reuben Keller
 */

package model.graph;

import java.util.*;

/**
 * Generates the minimum-spanning-tree (MST) of a weighted graph using
 * Kruskal's algorithm.
 *
 * @author Reuben Keller
 * @version Summer 2021
 */
public class KruskalMSTFinder<V> {

    /** A mapping of each vertex in the MST to its connected vertices. */
    private final Map<V, Set<V>> vertexMap;

    /** The set of Edges in the MST. */
    private Set<Edge<V>> myMST;


    /**
     * Constructs a Kruskal MST finder for the given Graph.
     */
    public KruskalMSTFinder() {
        vertexMap = new HashMap<>();
    }


    /**
     * Given a Graph, returns a list of its edges sorted in non-decreasing
     * order by weight.
     *
     * @param graph The Graph to extract a list of sorted edges from.
     * @return A list of Edges sorted in non-decreasing order by weight.
     */
    public List<Edge<V>> sortEdgesByWeight(Graph<V> graph) {
        List<Edge<V>> sortedEdges = new ArrayList<>(graph.edges());
        sortedEdges.sort(Comparator.comparingDouble(Edge::weight));
        return sortedEdges;
    }


    /**
     * Given a Graph, returns a UnionFindDisjointSet data structure containing
     * a disjoint set for each of its vertices.
     *
     * @param graph The Graph with vertices to make disjoint sets of.
     * @return A UnionFindDisjointSet data structure containing a disjoint set
     *     for each vertex in the given graph.
     */
    public UnionFindDisjointSet<V> makeDisjointSets(Graph<V> graph) {
        UnionFindDisjointSet<V> disjointSets = new UnionFindDisjointSet<>();
        for (V vertex : graph.vertices()) {
            disjointSets.makeSet(vertex);
        }
        return disjointSets;
    }


    /**
     * Adds the given vertices to the vertex map if they're not already in it.
     *
     * @param from The vertex a in edge (a, b).
     * @param to The vertex b in edge (a, b).
     */
    public void addToVertexMap(final V from, final V to) {
        vertexMap.computeIfAbsent(from, k -> new HashSet<>());
        vertexMap.get(from).add(to);
        vertexMap.computeIfAbsent(to, k -> new HashSet<>());
        vertexMap.get(to).add(from);
    }


    /**
     * Generates and returns a set of Edges in a MST using an efficient version
     * of Kruskal's algorithm.
     *
     * @param graph The Graph to find an MST of.
     * @return The MST of graph.
     */
    public Set<Edge<V>> findMST(Graph<V> graph) {
        myMST = new HashSet<>();
        List<Edge<V>> sortedEdges = sortEdgesByWeight(graph);
        UnionFindDisjointSet<V> disjointSets = makeDisjointSets(graph);
        // iterate through ordered edges to find a potential MST
        for (Edge<V> edge : sortedEdges) {
            if (myMST.size() == graph.numVertices()- 1) {
                // stop iterating early if |E| = |V| - 1
                break;
            }
            V from = edge.from();
            V to = edge.to();
            int fromMST = disjointSets.findSet(from);
            int toMST = disjointSets.findSet(to);
            if (fromMST != toMST) {
                myMST.add(edge);
                disjointSets.union(from, to);
                addToVertexMap(from, to);
            }
        }
        return myMST;
    }


    /**
     * Returns a mapping of each vertex in the MST to its connected vertices.
     *
     * @return A mapping of each vertex in the MST to its connected vertices.
     */
    public Map<V, Set<V>> getVertexMap () {
        return vertexMap;
    }

}
