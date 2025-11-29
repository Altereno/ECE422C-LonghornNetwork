import java.util.*;

/**
 * Concrete student implementation used throughout the Longhorn Network.
 * This class extends {@link Student}
 */
public class UniversityStudent extends Student {

    /* Current assigned roommate */
    private UniversityStudent currentRoommate;

    /**
     * Create a {@code UniversityStudent}.
     *
     * @param name                the student's full name
     * @param age                 the student's age in years
     * @param gender              the student's gender
     * @param year                the academic year (e.g., 1..4)
     * @param major               the student's declared major
     * @param gpa                 the student's grade point average
     * @param roommatePreferences ordered list of roommate preference names (may be null)
     * @param previousInternships list of previous internship company names (may be null)
     */
    public UniversityStudent(String name, int age, String gender, int year, String major, double gpa, List<String> roommatePreferences, List<String> previousInternships) {
        this.currentRoommate = null;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.year = year;
        this.major = major;
        this.gpa = gpa;
        this.roommatePreferences = roommatePreferences == null ? new ArrayList<>() : new ArrayList<>(roommatePreferences);
        this.previousInternships = previousInternships == null ? new ArrayList<>() : new ArrayList<>(previousInternships);
    }

    /**
     * Returns the currently assigned roommate for this student.
     *
     * @return the assigned {@link UniversityStudent} roommate, or null
     */
    public UniversityStudent getRoommate() {
        return currentRoommate;
    }

    /**
     * Sets the roommate for this student.
     *
     * @param roommate the {@link UniversityStudent} to assign as this student's roommate
     */
    public void setRoommate(UniversityStudent roommate) {
        currentRoommate = roommate;
    }

    /**
     * Compute an integer representing connection strength between this student
     * and another student.
     * 
     * Roommate: Add 4 if they are roommates.
     * Shared Internships: Add 3 for each shared internship.
     * Same Major: Add 2 if they share the same major.
     * Same Age: Add 1 if they are the same age.
     *
     * @param other the other student to compare against
     * @return integer weight representing connection strength
     */
    @Override
    public int calculateConnectionStrength(Student other) {
        int connectionStrength = 0;

        if(other instanceof UniversityStudent) {
            UniversityStudent o = (UniversityStudent) other;
            if(this.currentRoommate != null && this.currentRoommate.equals(o)) {
                connectionStrength += 4;
            }

            for(String internship : previousInternships) {
                if(o.previousInternships.contains(internship)) {
                    connectionStrength += 3;
                }
            }

            if(this.major.equals(o.major)) {
                connectionStrength += 2;
            }

            if(this.age == o.age) {
                connectionStrength += 1;
            }
        }
        
        return connectionStrength;
    }

    /**
     * Returns a string representation of this UniversityStudent.
     *
     * @return a formatted string with all student attributes
     */
    @Override
    public String toString() {
        return String.format("UniversityStudent{name='%s', age=%d, gender='%s', year=%d, major='%s', GPA=%.1f, roommatePreferences=%s, previousInternships=%s}",
                name, age, gender, year, major, gpa, roommatePreferences, previousInternships);
    }
}

