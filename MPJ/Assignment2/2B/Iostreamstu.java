import java.io.*;
import java.util.*;

public class Iostreamstu {
    private static final String FILE_NAME = "marks.txt";

    public static void main(String[] args) throws IOException {
        Map<String, List<Integer>> studentMarks = readMarksFromFile();
        
        System.out.println("\nStudent Average Marks:");
        for (var entry : studentMarks.entrySet()) {
            double average = entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);
            System.out.println(entry.getKey() + ": " + average);
        }
    }

    private static Map<String, List<Integer>> readMarksFromFile() throws IOException {
        Map<String, List<Integer>> studentMarks = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            String studentName = parts[0];
            int marks = Integer.parseInt(parts[2]);

            studentMarks.putIfAbsent(studentName, new ArrayList<>());
            studentMarks.get(studentName).add(marks);
        }
        reader.close();
        return studentMarks;
    }
}