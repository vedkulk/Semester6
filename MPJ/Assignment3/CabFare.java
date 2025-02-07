class Fare {
    public double calculateFare(double distance) {
        return distance * 10;
    }

    public double calculateFare(String rideType, double distance) {
        double rate = rideType.equals("Luxury") ? 20 : 10;
        return distance * rate;
    }
}

public class CabFare {
        public static void main(String[] args) {
        Fare fare = new Fare();
        System.out.println(fare.calculateFare(10));
        System.out.println(fare.calculateFare("Luxury", 10));
    }
}
