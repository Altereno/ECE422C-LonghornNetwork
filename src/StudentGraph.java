import java.util.*;

/**
 * represents the relationships between students as a weighted graph. 
 * Each student is a node, and the connection strength between 
 * two students is an edge with a corresponding weight.
 */
public class StudentGraph {

    public class Edge {
        public UniversityStudent neighbor;
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
    }

    private Map<UniversityStudent, List<Edge>> adj = new HashMap<>();

    /**
     * Construct a StudentGraph.
     *
     * @param students list of students
     */
    public StudentGraph(List<UniversityStudent> students) {
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
        return new ArrayList<>(adj.getOrDefault(student, Collections.emptyList()));
    }

    /**
     * Prints the graph.
     */
    public void displayGraph() {
    }
}
