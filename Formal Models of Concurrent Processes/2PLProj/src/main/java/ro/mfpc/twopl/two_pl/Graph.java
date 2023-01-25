package ro.mfpc.twopl.two_pl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Graph {

    private List<Vertex> vertices;

    public Graph() {
        this.vertices = new ArrayList<>();
    }

    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public void addVertex(Vertex vertex) {
        this.vertices.add(vertex);
    }

    public Vertex getVertex(Transaction t) {

        return vertices.stream().filter(v -> v.getTransaction().equals(t)).findAny().get();
    }

    public void removeVertex(Vertex vertex) {
        this.vertices.remove(vertex);
        this.removeVertexEdges(vertex);
    }

    public void addEdge(Vertex from, Vertex to) {
        from.addNeighbour(to);
    }

    private void removeVertexEdges(Vertex vertex) {
        vertices.forEach(v -> v.removeNeighbour(vertex));
    }

    public Vertex hasCycle() {
        for (Vertex vertex : vertices) {
            if (!vertex.isVisited() && hasCycle(vertex)) {
                return vertex;
            }
        }
        return null;
    }

    private boolean hasCycle(Vertex sourceVertex) {
        sourceVertex.setBeingVisited(true);

        for (Vertex neighbour : sourceVertex.getAdjacencyList()) {
            if (neighbour.isBeingVisited()) {
                // backward edge exists
                return true;
            } else if (!neighbour.isVisited() && hasCycle(neighbour)) {
                return true;
            }
        }

        sourceVertex.setBeingVisited(false);
        sourceVertex.setVisited(true);
        return false;
    }
}
