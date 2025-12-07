package main.java.com.longhorn;
import java.io.*;
import java.util.*;

/**
 * Utility class for parsing student data files and returning a
 * list of {@link UniversityStudent} objects.
 */
public class DataParser {
    /** Array of expected field names for student records */
    private static final String[] recordKeys = {
        "Name",
        "Age",
        "Gender",
        "Year",
        "Major",
        "GPA",
        "RoommatePreferences",
        "PreviousInternships"
    };

    /**
     * Parses a file containing student records and produces a list of
     * {@link UniversityStudent} objects.
     *
     * @param filename path to the input file to parse
     * @return a list of parsed UniversityStudent instances
     * @throws IOException if there is an issue reading the file or malformed input
     */
    public static List<UniversityStudent> parseStudents(String filename) throws IOException {
        List<UniversityStudent> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = null;
            while((line = br.readLine()) != null) {
                line = line.trim();
                if(line.equals("Student:")) {
                    String name = null;
                    int age = Integer.MIN_VALUE;
                    String gender = null;
                    int year = Integer.MIN_VALUE;
                    String major = null;
                    double gpa = Double.MIN_VALUE;
                    ArrayList<String> roommatePrefernces = null;
                    ArrayList<String> previousInternships = null;
                    for(int i = 0; i < recordKeys.length; i++) {
                        String currKV = br.readLine().trim();

                        String[] split = currKV.split(":", 2);
                        if(split.length <= 1) {
                            System.err.println("Parsing error: Incorrect format in line: '" + currKV + "'. Expected format 'Name: <value>'.");
                            return students;
                        }
                        String currKey = split[0].trim();
                        String currVal = split[1].trim();

                        if(!currKey.equals(recordKeys[i])) {
                            System.err.println("Parsing error: Missing required field '" + recordKeys[i] + "' in student entry for " + name + ".");
                            return students;
                        }

                        switch (currKey) {
                            case "Name":
                                name = currVal;
                                break;

                            case "Age":
                                try {
                                    age = Integer.parseInt(currVal);
                                } catch (NumberFormatException e) {
                                    System.err.println("Number format error: Invalid number format for age: '" + currVal + "' in student entry for " + name + ".");
                                    return students;
                                }
                                break;

                            case "Gender":
                                gender = currVal;
                                break;

                            case "Year":
                                try {
                                    year = Integer.parseInt(currVal);
                                } catch (NumberFormatException e) {
                                    System.err.println("Number format error: Invalid number format for year: '" + currVal + "' in student entry for " + name + ".");
                                    return students;
                                }
                                break;

                            case "Major":
                                major = currVal;
                                break;

                            case "GPA":
                                try {
                                    gpa = Double.parseDouble(currVal);
                                } catch (NumberFormatException e) {
                                    System.err.println("Number format error: Invalid number format for GPA: '" + currVal + "' in student entry for " + name + ".");
                                    return students;
                                }
                                break;

                            case "RoommatePreferences":
                                roommatePrefernces = parseCommaSeparatedString(currVal);
                                break;

                            case "PreviousInternships":
                                previousInternships = parseCommaSeparatedString(currVal);
                                break;

                            default:
                                System.err.println("Missing case");
                                break;
                        }
                    }
                    UniversityStudent s = new UniversityStudent(name, age, gender, year, major, gpa, roommatePrefernces, previousInternships);
                    students.add(s);
                }
            }
        } catch (IOException e) {
            throw e;
        }

        return students;
    }

    /**
     * Parses a comma-separated string into a list of trimmed strings.
     * Handles the special case where the input is "None" by returning an empty list.
     *
     * @param in the comma-separated string to parse
     * @return an ArrayList of trimmed string values, or an empty list if input is "None"
     */
    private static ArrayList<String> parseCommaSeparatedString(String in) {
        ArrayList<String> stringAsList = new ArrayList<>();

        if(in.equals("None")) {
            return stringAsList;
        }

        String[] split = in.split(",");
        for(String s : split) stringAsList.add(s.trim());
        return stringAsList;   
    }
}