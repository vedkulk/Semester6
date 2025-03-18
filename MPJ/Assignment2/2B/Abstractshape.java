import java.util.Scanner;


abstract class Shape {
   abstract double calculateArea();
}


class Rectangle extends Shape {
   private double length;
   private double breadth;


   public Rectangle(double length, double breadth) {
       this.length = length;
       this.breadth = breadth;
   }


   @Override
   double calculateArea() {
       return length * breadth;
   }
}


class Triangle extends Shape {
   private double base;
   private double height;


   public Triangle(double base, double height) {
       this.base = base;
       this.height = height;
   }


   @Override
   double calculateArea() {
       return 0.5 * base * height;
   }
}


public class Abstractshape {
   public static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);


       System.out.print("Enter length of rectangle: ");
       double length = scanner.nextDouble();
       System.out.print("Enter breadth of rectangle: ");
       double breadth = scanner.nextDouble();


       Shape rectangle = new Rectangle(length, breadth);
       System.out.println("Area of Rectangle: " + rectangle.calculateArea());


       System.out.print("Enter base of triangle: ");
       double base = scanner.nextDouble();
       System.out.print("Enter height of triangle: ");
       double height = scanner.nextDouble();

       Shape triangle = new Triangle(base, height);
       System.out.println("Area of Triangle: " + triangle.calculateArea());


       scanner.close();
   }
}
