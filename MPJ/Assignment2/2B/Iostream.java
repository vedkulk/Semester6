import java.io.*;
import java.util.Scanner;


class EmployeeSalaryRecord {


   // Method to write employee records to a file
   public void wempdetails(String name, int id, double salary) {
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
           e.printStackTrace();                   //The e.printStackTrace() method prints the stack trace of the exception to the console.
       }
   }


   // Method to read and display employee records from the file
   public void dispemprecords() {
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


public class Iostream {
   public static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);
       EmployeeSalaryRecord record = new EmployeeSalaryRecord();


       // Taking user input for employee details
       System.out.print("Enter Employee Name: ");
       String name = scanner.nextLine();


       System.out.print("Enter Employee ID: ");
       int id = scanner.nextInt();


       System.out.print("Enter Employee Salary: ");
       double salary = scanner.nextDouble();


       // Saving employee details to the file
       record.wempdetails(name, id, salary);


       // Reading and displaying saved employee details
       System.out.println("\nDisplaying Employee Records : -");
       record.dispemprecords();


       scanner.close();
   }
}
