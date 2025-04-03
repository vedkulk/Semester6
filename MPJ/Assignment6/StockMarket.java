package Assignment6;

import java.util.Scanner;

class Stock {
    private double stockPrice;
    private boolean updated = false;
    private boolean running = true;  

    public synchronized void setPrice(double price) {
        if (price == -1) {  
            running = false;
            notifyAll();  
            return;
        }
        stockPrice = price;
        updated = true;
        notifyAll(); 
    }

    public synchronized double getPrice() {
        while (!updated && running) {
            try {
                wait();  
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
                return -1;
            }
        }
        if (!running) return -1; 
        updated = false;
        return stockPrice;
    }

    public boolean isRunning() {
        return running;
    }
}

class InputThread extends Thread {
    private Stock market;
    private Scanner scanner;

    public InputThread(Stock market, Scanner scanner) {
        this.market = market;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        System.out.println("Input Thread ID: " + this.getId());
        while (market.isRunning()) {
            System.out.print("Enter new stock price (or -1 to stop): ");
            double price = scanner.nextDouble();
            market.setPrice(price);
            if (price == -1) break;  
        }
    }
}

class OutputThread extends Thread {
    private Stock market;

    public OutputThread(Stock market) {
        this.market = market;
    }

    @Override
    public void run() {
        System.out.println("Output Thread ID: " + this.getId());
        while (market.isRunning()) {
            double price = market.getPrice();
            if (price == -1) break;  
            System.out.println("Latest stock price: " + price);
        }
        System.out.println("Output Thread Exiting...");
    }
}

public class StockMarket {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Stock market = new Stock();

        InputThread inputThread = new InputThread(market, scanner);
        OutputThread outputThread = new OutputThread(market);

        outputThread.start();
        inputThread.start();

        try {
            inputThread.join();  
            outputThread.join(); 
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
        }

        scanner.close();
        System.out.println("Stock Market Program Exited.");
    }
}
