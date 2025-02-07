import java.io.*;
import java.util.Scanner;

class EmployeeSalaryRecord {
    public void writeDetails(String name, int id, double salary) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("employees.txt", true))) {
            writer.write("Employee Name: " + name);
            writer.newLine();
            writer.write("Employee ID: " + id);
            writer.newLine();
            writer.write("Salary: " + salary);
            writer.newLine();
            writer.write("-------------------------------");
            writer.newLine();
            System.out.println("Employee details saved successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while saving employee details.");
            e.printStackTrace();
        }
    }
    public void displayEmployees() {
        try (BufferedReader reader = new BufferedReader(new FileReader("employees.txt"))) {
            String line;
            System.out.println("Reading Employee Records...");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading employee records.");
            e.printStackTrace();
        }
    }
}

public class EmpRecord {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EmployeeSalaryRecord record = new EmployeeSalaryRecord();
        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Employee ID: ");
        int id = scanner.nextInt();
        System.out.print("Enter Employee Salary: ");
        double salary = scanner.nextDouble();
        record.writeDetails(name, id, salary);
        System.out.println("\nDisplaying Employee Records : -");
        record.displayEmployees();
        scanner.close();
    }
}