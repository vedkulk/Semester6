import java.util.*;

class StudentGrades {
    public double calculateGPA(int[] marks) {
        if (marks.length == 0) {
            throw new ArithmeticException("Cannot calculate GPA for zero subjects.");
        }
        int total = 0;
        for (int i =0;i<marks.length;i++) {
            if (marks[i] < 0 || marks[i] > 100) {
                throw new IllegalArgumentException("Marks should be between 0 and 100.");
            }
            total += marks[i];
        }
        return (double) total / marks.length;
    }
}

public class StudentMarks {
    public static void main() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Enter number of subjects: ");
            int n = sc.nextInt();
            int[] marks = new int[n];

            for (int i = 0; i < n; i++) {
                System.out.print("Enter marks for subject " + (i + 1) + ": ");
                marks[i] = sc.nextInt();
            }

            StudentGrades student = new StudentGrades();
            double gpa = student.calculateGPA(marks);
            System.out.println("Calculated GPA: " + gpa);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Invalid index access.");
        } catch (ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}
