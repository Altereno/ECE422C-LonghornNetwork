import java.util.*;

/**
 * ReferralPathFinder searches for a chain of student-to-student referrals
 * from a starting student to any student who listed a particular
 * {@code targetCompany} in their previous internships.
 */
public class ReferralPathFinder {
    /**
     * Create a finder bound to a {@link StudentGraph}.
     *
     * @param graph the student relationship graph used for path searches
     */
    public ReferralPathFinder(StudentGraph graph) {
        // Constructor
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
        // Method signature only
        return new ArrayList<>();
    }
}
