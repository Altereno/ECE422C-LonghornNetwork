package com.longhorn;
import java.util.*;

/**
 * represents the relationships between students as a weighted graph. 
 * Each student is a node, and the connection strength between 
 * two students is an edge with a corresponding weight.
 */
public class StudentGraph {

    /**
     * Represents a weighted edge in the student graph connecting two students.
     */
    public class Edge {
        /** The neighboring student in this edge */
        public UniversityStudent neighbor;
        /** The weight of the connection between students */
        public int weight;

        /**
         * Create an edge record pointing to a neighboring student with the
         * provided weight.
         *
         * @param neighbor target neighbor node
         * @param weight   integer edge weight
         */
        public Edge(UniversityStudent neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "(" + neighbor.name + ", " + weight + ")";
        }

    }

    /** Adjacency list representation of the student graph */
    private Map<UniversityStudent, List<Edge>> adj = new HashMap<>();

    /**
     * Construct a StudentGraph.
     *
     * @param students list of students
     */
    public StudentGraph(List<UniversityStudent> students) {
        for(UniversityStudent s : students) {
            adj.put(s, new ArrayList<>());
        }

        for(int i = 0; i < students.size(); i++) {
            for(int j = i + 1; j < students.size(); j++) {
                UniversityStudent s1 = students.get(i);
                UniversityStudent s2 = students.get(j);
                int weight = s1.calculateConnectionStrength(s2);
                if(weight > 0) {
                    adj.get(s1).add(new Edge(s2, weight));
                    adj.get(s2).add(new Edge(s1, weight));
                }
            }
        }
    }

    /**
     * Returns all student nodes present in the graph.
     *
     * @return a set containing all UniversityStudent nodes
     */
    public Set<UniversityStudent> getAllNodes() {
        return adj.keySet();
    }

    /**
     * Returns the neighbor list for the given student.
     *
     * @param student the student whose neighbors are requested
     * @return a list of Edge objects
     */
    public List<Edge> getNeighbors(UniversityStudent student) {
        return adj.get(student);
    }

    /**
     * Prints the graph.
     */
    public void displayGraph() {
        System.out.println("\nStudent Graph:");
        for(UniversityStudent s : adj.keySet()) {
            System.out.println(s.name + " -> " + adj.get(s));
        }
    }
}
