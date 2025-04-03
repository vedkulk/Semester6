package Assignment6;

import java.util.Scanner;

class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}

class TicketBooking {
    private int seats;

    public TicketBooking(int seats) {
        this.seats = seats;
    }

    public synchronized boolean bookSeat(String name, int numSeats) throws InvalidAmountException {
        if (numSeats <= 0)
            throw new InvalidAmountException("Seat number cannot be zero or negative.");
        if (numSeats <= seats) {
            System.out.println(name + " booked " + numSeats + " seat(s).");
            seats -= numSeats;
            System.out.println("Seats remaining: " + seats);
            return true;
        } else {
            System.out.println(name + " failed to book " + numSeats + " seat(s). Not enough available.");
            return false;
        }
    }
}

class Passenger implements Runnable {
    private TicketBooking booking;
    private String name;
    private int numSeats;

    public Passenger(TicketBooking booking, String name, int numSeats) {
        this.booking = booking;
        this.name = name;
        this.numSeats = numSeats;
    }

    @Override
    public void run() {
        try {
            booking.bookSeat(name, numSeats);
        } catch (InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}

public class RailwayBooking {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Enter total available seats: ");
        int totalSeats = sc.nextInt();
        TicketBooking booking = new TicketBooking(totalSeats);

        System.out.print("Enter passenger 1 name: ");
        String name1 = sc.next();
        System.out.print("Enter tickets for " + name1 + ": ");
        int seats1 = sc.nextInt();

        System.out.print("Enter passenger 2 name: ");
        String name2 = sc.next();
        System.out.print("Enter tickets for " + name2 + ": ");
        int seats2 = sc.nextInt();

        System.out.print("Enter passenger 3 name: ");
        String name3 = sc.next();
        System.out.print("Enter tickets for " + name3 + ": ");
        int seats3 = sc.nextInt();
        
                Thread t1 = new Thread(new Passenger(booking, name1, seats1));
                Thread t2 = new Thread(new Passenger(booking, name2, seats2));
                Thread t3 = new Thread(new Passenger(booking, name3, seats3));
        
                t1.start();
                System.out.println("Thread ID for " + name1 + ": " + t1.getId());
                t2.start();
                System.out.println("Thread ID for " + name2 + ": " + t2.getId());
                t3.start();
                System.out.println("Thread ID for " + name3 + ": " + t3.getId());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }        

        sc.close();
    }
}