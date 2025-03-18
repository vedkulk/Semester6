import java.util.Scanner;

abstract class Employee {
    protected String name;
    protected int id;
    protected double baseSalary;

    public Employee(String name, int id, double baseSalary) {
        this.name = name;
        this.id = id;
        this.baseSalary = baseSalary;
    }

    public abstract double calculateSalary();
}

class FullTimeEmployee extends Employee {
    private double bonus;

    public FullTimeEmployee(String name, int id, double baseSalary, double bonus) {
        super(name, id, baseSalary);
        this.bonus = bonus;
    }

    @Override
    public double calculateSalary() {
        return baseSalary + bonus;
    }
}

class PartTimeEmployee extends Employee {
    private double hourlyRate;
    private int hoursWorked;

    public PartTimeEmployee(String name, int id, double baseSalary, double hourlyRate, int hoursWorked) {
        super(name, id, baseSalary);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculateSalary() {
        return hourlyRate * hoursWorked;
    }
}

public class Abstractemp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("Enter details for Full-Time Employee:");
        System.out.print("Name: ");
        String fullName = scanner.nextLine();
        System.out.print("Employee ID: ");
        int fullId = scanner.nextInt();
        System.out.print("Base Salary: ");
        double fullBaseSalary = scanner.nextDouble();
        System.out.print("Bonus: ");
        double bonus = scanner.nextDouble();

        FullTimeEmployee fullTimeEmployee = new FullTimeEmployee(fullName, fullId, fullBaseSalary, bonus);

        System.out.println("\nEnter details for Part-Time Employee:");
        scanner.nextLine();  
        System.out.print("Name: ");
        String partName = scanner.nextLine();
        System.out.print("Employee ID: ");
        int partId = scanner.nextInt();
        System.out.print("Base Salary (ignored for Part-Time): ");
        double partBaseSalary = scanner.nextDouble();  
        System.out.print("Hourly Rate: ");
        double hourlyRate = scanner.nextDouble();
        System.out.print("Hours Worked: ");
        int hoursWorked = scanner.nextInt();

        PartTimeEmployee partTimeEmployee = new PartTimeEmployee(partName, partId, partBaseSalary, hourlyRate, hoursWorked);

        System.out.println("\nFinal Salary of Full-Time Employee: " + fullTimeEmployee.calculateSalary());
        System.out.println("Final Salary of Part-Time Employee: " + partTimeEmployee.calculateSalary());

        scanner.close();
    }
}

