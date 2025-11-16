import java.util.*;

/**
 * Base abstract class representing a generic student in the Longhorn Network.
 */
public abstract class Student {
    /** Student's full name */
    protected String name;
    /** Student's age in years */
    protected int age;
    /** Student's gender as a string */
    protected String gender;
    /** Academic year */
    protected int year;
    /** Student's declared major */
    protected String major;
    /** Grade point average */
    protected double gpa;
    /** Ordered list of roommate preferences by name */
    protected List<String> roommatePreferences;
    /** List of previous internships */
    protected List<String> previousInternships;

    /**
     * Calculate a numeric connection strength between this student and another.
     * Higher values indicate stronger relationships.
     *
     * @param other the student to compare against
     * @return an integer weight representing connection strength
     */
    public abstract int calculateConnectionStrength(Student other);
}
