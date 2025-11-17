import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * ResultManager.java
 * Main class with menu-driven UI to add/show student results.
 *
 * Compile: javac ResultManager.java
 * Run:     java ResultManager
 */

/* ------------------ Custom Checked Exception ------------------ */
class InvalidMarksException extends Exception {
    public InvalidMarksException(String message) {
        super(message);
    }
}

/* ------------------ Student Class ------------------ */
class Student {
    private Integer rollNumber;
    private String studentName;
    private Integer[] marks; // marks for 3 subjects

    public Student(Integer rollNumber, String studentName, Integer[] marks) throws InvalidMarksException {
        if (rollNumber == null) {
            throw new IllegalArgumentException("Roll number cannot be null.");
        }
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be null or empty.");
        }
        if (marks == null || marks.length != 3) {
            throw new IllegalArgumentException("Marks array must contain exactly 3 integers.");
        }
        this.rollNumber = rollNumber;
        this.studentName = studentName.trim();
        this.marks = new Integer[3];
        // Validate and copy marks
        for (int i = 0; i < 3; i++) {
            int m = marks[i];
            validateSingleMark(m, i + 1); // may throw InvalidMarksException
            this.marks[i] = m;
        }
    }

    // Validate all marks (not strictly needed since constructor validates each, but provided)
    public void validateMarks() throws InvalidMarksException {
        for (int i = 0; i < marks.length; i++) {
            validateSingleMark(marks[i], i + 1);
        }
    }

    // helper: validate one mark
    private void validateSingleMark(int mark, int subjectNumber) throws InvalidMarksException {
        if (mark < 0 || mark > 100) {
            throw new InvalidMarksException("Invalid marks for subject " + subjectNumber + ": " + mark +
                                            ". Allowed range is 0 to 100.");
        }
    }

    public double calculateAverage() {
        double sum = 0.0;
        for (Integer m : marks) {
            sum += m;
        }
        return sum / marks.length;
    }

    // Simple passing criteria: pass if all marks >= 40 (this can be changed easily)
    public String getResultStatus() {
        for (Integer m : marks) {
            if (m < 40) {
                return "Fail";
            }
        }
        return "Pass";
    }

    public Integer getRollNumber() {
        return rollNumber;
    }

    public void displayResult() {
        System.out.println("Roll Number: " + rollNumber);
        System.out.println("Student Name: " + studentName);
        System.out.print("Marks: ");
        for (int i = 0; i < marks.length; i++) {
            System.out.print(marks[i] + (i < marks.length - 1 ? " " : ""));
        }
        System.out.println();
        System.out.println("Average: " + calculateAverage());
        System.out.println("Result: " + getResultStatus());
    }
}

/* ------------------ ResultManager: UI and management ------------------ */
public class ResultManager {
    private static final int MAX_STUDENTS = 100; // fixed-size array for demonstration
    private Student[] students;
    private int studentCount;
    private Scanner scanner;

    public ResultManager() {
        students = new Student[MAX_STUDENTS];
        studentCount = 0;
        scanner = new Scanner(System.in);
    }

    // addStudent demonstrates throw of custom exception and handles input errors
    public void addStudent() {
        try {
            System.out.print("Enter Roll Number: ");
            int roll = readInt();

            // Check for duplicate roll
            if (findStudentIndexByRoll(roll) != -1) {
                System.out.println("Error: A student with roll number " + roll + " already exists.");
                return;
            }

            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Error: Student name cannot be empty.");
                return;
            }

            Integer[] marks = new Integer[3];
            for (int i = 0; i < 3; i++) {
                System.out.print("Enter marks for subject " + (i + 1) + ": ");
                marks[i] = readInt();
            }

            // Constructor will validate marks and throw InvalidMarksException (checked)
            Student s = new Student(roll, name, marks);
            if (studentCount < MAX_STUDENTS) {
                students[studentCount++] = s;
                System.out.println("Student added successfully. Returning to main menu...");
            } else {
                System.out.println("Error: Student storage is full.");
            }

        } catch (InvalidMarksException ime) {
            // Custom exception for invalid marks (checked)
            System.out.println("Error: " + ime.getMessage() + " Returning to main menu...");
        } catch (InputMismatchException ime2) {
            System.out.println("Input error: expected an integer. Clearing input and returning to main menu...");
            scanner.nextLine(); // clear bad token
        } catch (IllegalArgumentException iae) {
            System.out.println("Input error: " + iae.getMessage());
        } catch (Exception ex) {
            // catch-all to ensure robustness
            System.out.println("An unexpected error occurred: " + ex.getMessage());
        }
    }

    // showStudentDetails prompts for roll and displays result
    public void showStudentDetails() {
        try {
            System.out.print("Enter Roll Number to search: ");
            int roll = readInt();
            int idx = findStudentIndexByRoll(roll);
            if (idx == -1) {
                System.out.println("Student with roll number " + roll + " not found.");
            } else {
                students[idx].displayResult();
                System.out.println("Search completed.");
            }
        } catch (InputMismatchException ime) {
            System.out.println("Input error: expected an integer. Clearing input and returning to main menu...");
            scanner.nextLine(); // clear bad token
        } catch (Exception ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        }
    }

    // helper to find student index by roll, returns -1 if not found
    private int findStudentIndexByRoll(int roll) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getRollNumber() == roll) {
                return i;
            }
        }
        return -1;
    }

    // helper to safely read integers and avoid InputMismatchException leaking out to main loop
    // This method throws InputMismatchException which the callers handle.
    private int readInt() throws InputMismatchException {
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    // menu loop
    public void mainMenu() {
        boolean running = true;
        try {
            while (running) {
                System.out.println("===== Student Result Management System =====");
                System.out.println("1. Add Student");
                System.out.println("2. Show Student Details");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice;
                try {
                    choice = readInt();
                } catch (InputMismatchException ime) {
                    System.out.println("Invalid choice. Please enter an integer between 1 and 3.");
                    scanner.nextLine(); // clear bad token
                    continue;
                }

                switch (choice) {
                    case 1:
                        addStudent();
                        break;
                    case 2:
                        showStudentDetails();
                        break;
                    case 3:
                        System.out.println("Exiting program. Thank you!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please select 1, 2 or 3.");
                }
            }
        } finally {
            // finally block will always execute; good place to release resources
            if (scanner != null) {
                scanner.close();
            }
            System.out.println("Scanner closed. Program terminated.");
        }
    }

    // main
    public static void main(String[] args) {
        ResultManager manager = new ResultManager();
        manager.mainMenu();
    }
}
