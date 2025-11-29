import java.util.*;

/**
 * Implements the Gale-Shapley stable matching algorithm to assign
 * roommates among a list of {@link UniversityStudent} instances based on
 * their {@code roommatePreferences}.
 */
public class GaleShapley {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private GaleShapley() {
    }

    /**
     * Assigns roommates for the supplied list of students.
     * 
     * @param students list of students to match into roommate pairs
     */
    public static void assignRoommates(List<UniversityStudent> students) {
        Map<UniversityStudent, UniversityStudent> roommatePairs = new HashMap<>();
        Map<UniversityStudent, Integer> nextProposalIndex = new HashMap<>();
        Map<String, UniversityStudent> nameToStudent = new HashMap<>();

        for(UniversityStudent s : students) {
            nameToStudent.put(s.name, s);
            nextProposalIndex.put(s, 0);
        }

        Queue<UniversityStudent> freeStudents = new LinkedList<>();
        for(UniversityStudent s : students) {
            if(!s.roommatePreferences.isEmpty()) {
                freeStudents.offer(s);
            }
        }

        while(!freeStudents.isEmpty()) {
            UniversityStudent s = freeStudents.poll();
            if(s.getRoommate() != null) {
                continue;
            }

            int index = nextProposalIndex.get(s);
            if(index > s.roommatePreferences.size()) {
                continue;
            }

            String preferredName = s.roommatePreferences.get(index);
            nextProposalIndex.put(s, index + 1);
            UniversityStudent t = nameToStudent.get(preferredName);
            if(t == null) {
                if(nextProposalIndex.get(s) < s.roommatePreferences.size()) {
                    freeStudents.offer(s);
                }
                continue;
            }

            if(!t.roommatePreferences.contains(s.name)) {
                if(nextProposalIndex.get(s) < s.roommatePreferences.size()) {
                    freeStudents.offer(s);
                }
                continue;
            }

            if(t.getRoommate() == null) {
                roommatePairs.put(s, t);
                roommatePairs.put(t, s);
                t.setRoommate(s);
                s.setRoommate(t);
            } else {
                UniversityStudent currentRoommate = t.getRoommate();
                int currentIndex = t.roommatePreferences.indexOf(currentRoommate.name);
                int proposalIndex = t.roommatePreferences.indexOf(s.name);
                if(proposalIndex < currentIndex) {
                    roommatePairs.remove(currentRoommate);
                    freeStudents.offer(currentRoommate);
                    currentRoommate.setRoommate(null);

                    roommatePairs.put(s, t);
                    roommatePairs.put(t, s);
                    t.setRoommate(s);
                    s.setRoommate(t);
                } else {
                    if(nextProposalIndex.get(s) < s.roommatePreferences.size()) {
                        freeStudents.offer(s);
                    }
                }

            }
        }

        System.out.println("\nRoommate Pairings (Gale-Shapely):");
        Set<UniversityStudent> printed = new HashSet<>();
        for(UniversityStudent s : roommatePairs.keySet()) {
            UniversityStudent p = roommatePairs.get(s);
            if(!printed.contains(s) && !printed.contains(p)) {
                System.out.println(s.name + " paired with " + p.name);
                printed.add(s);
                printed.add(p);
            }
        }
    }
}
