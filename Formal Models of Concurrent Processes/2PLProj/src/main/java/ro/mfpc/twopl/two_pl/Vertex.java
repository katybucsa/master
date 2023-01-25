package ro.mfpc.twopl.two_pl;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

    private Transaction transaction;

    private boolean visited;

    private boolean beingVisited;

    private List<Vertex> adjacencyList;

    public Vertex(Transaction transaction) {
        this.transaction = transaction;
        this.adjacencyList = new ArrayList<>();
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setLabel(Transaction transaction) {
        this.transaction = transaction;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isBeingVisited() {
        return beingVisited;
    }

    public void setBeingVisited(boolean beingVisited) {
        this.beingVisited = beingVisited;
    }

    public List<Vertex> getAdjacencyList() {
        return adjacencyList;
    }

    public void setAdjacencyList(List<Vertex> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public void addNeighbour(Vertex adjacent) {
        this.adjacencyList.add(adjacent);
    }

    public void removeNeighbour(Vertex adjacent) {
        this.adjacencyList.remove(adjacent);
    }
}
