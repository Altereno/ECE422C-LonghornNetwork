import java.util.*;

/**
 * ReferralPathFinder searches for a chain of student-to-student referrals
 * from a starting student to any student who listed a particular
 * {@code targetCompany} in their previous internships.
 */
public class ReferralPathFinder {
    private StudentGraph graph;
    /**
     * Create a finder bound to a {@link StudentGraph}.
     *
     * @param graph the student relationship graph used for path searches
     */
    public ReferralPathFinder(StudentGraph graph) {
        this.graph = graph;
    }

    /**
     * Attempts to find a referral path from {@code start} to any student who
     * lists {@code targetCompany} among their {@code previousInternships}.
     *
     * @param start         the student where the search begins
     * @param targetCompany the company name to search for in internships
     * @return an ordered list of students representing the referral chain
     */
    public List<UniversityStudent> findReferralPath(UniversityStudent start, String targetCompany) {
        Map<UniversityStudent, Double> dist = new HashMap<>();
        Map<UniversityStudent, UniversityStudent> prev = new HashMap<>();
        Set<UniversityStudent> visited = new HashSet<>();

        for(UniversityStudent s : graph.getAllNodes()) {
            dist.put(s, Double.MAX_VALUE);
            prev.put(s, null);
        }
        dist.put(start, 0.0);

        PriorityQueue<UniversityStudent> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(start);

        while(!pq.isEmpty()) {
            UniversityStudent u = pq.poll();
            if(visited.contains(u)) continue;
            visited.add(u);

            for(String internship : u.previousInternships) {
                if(internship.equalsIgnoreCase(targetCompany)) {
                    List<UniversityStudent> path = new ArrayList<>();
                    UniversityStudent curr = u;
                    while(curr != null) {
                        path.add(curr);
                        curr = prev.get(curr);
                    }
                    Collections.reverse(path);
                    return path;
                }
            }

            for(StudentGraph.Edge e : graph.getNeighbors(u)) {
                UniversityStudent v = e.neighbor;
                if(visited.contains(v)) continue;

                double newDist = dist.get(u) + (1.0 / e.weight);
                if(newDist < dist.get(v)) {
                    dist.put(v, newDist);
                    prev.put(v, u);
                    pq.add(v);
                }
            }
        }

        return new ArrayList<>();
    }
}
