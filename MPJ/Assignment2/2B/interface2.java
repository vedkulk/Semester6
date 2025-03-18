
interface Transport {
    void start();  
    void stop();  
}

class Bus implements Transport {
    private String busNumber;

    public Bus(String busNumber) {
        this.busNumber = busNumber;
    }

    @Override
    public void start() {
        System.out.println("Bus " + busNumber + " is now starting.");
    }

    @Override
    public void stop() {
        System.out.println("Bus " + busNumber + " has stopped.");
    }

    public void displayDetails() {
        System.out.println("Bus Number: " + busNumber);
    }
}

class Train implements Transport {
    private String trainNumber;

    public Train(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    @Override
    public void start() {
        System.out.println("Train " + trainNumber + " is now starting.");
    }

    @Override
    public void stop() {
        System.out.println("Train " + trainNumber + " has stopped.");
    }
    public void displayDetails() {
        System.out.println("Train Number: " + trainNumber);
    }
}


public class Interface {
    public static void main(String[] args) {
        Bus bus = new Bus("101");
        Train train = new Train("Express 500");

        System.out.println("Bus Details:");
        bus.displayDetails();
        bus.start();
        bus.stop();

        System.out.println();

        System.out.println("Train Details:");
        train.displayDetails();
        train.start();
        train.stop();
    }
}
