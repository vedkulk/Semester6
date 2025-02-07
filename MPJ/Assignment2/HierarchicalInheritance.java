class Vehicle {
    String brand;
    int speed;

    public Vehicle(String brand, int speed) {
        this.brand = brand;
        this.speed = speed;
    }

    public void displayDetails() {
        System.out.println("Brand: " + brand);
        System.out.println("Speed: " + speed + " km/h");
    }
}

// Derived class Car
class Car extends Vehicle {
    String fuelType;

    public Car(String brand, int speed, String fuelType) {
        super(brand, speed);
        this.fuelType = fuelType;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Fuel Type: " + fuelType);
    }
}

// Derived class Bike
class Bike extends Vehicle {
    int engineCapacity;

    public Bike(String brand, int speed, int engineCapacity) {
        super(brand, speed);
        this.engineCapacity = engineCapacity;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Engine Capacity: " + engineCapacity + " cc");
    }
}

public class HierarchicalInheritance {
    public static void main(String[] args) {
        Car car = new Car("Toyota", 180, "Petrol");
        car.displayDetails();
        
        System.out.println();
        
        Bike bike = new Bike("Yamaha", 120, 150);
        bike.displayDetails();
    }
}
