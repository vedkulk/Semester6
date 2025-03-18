
interface Transport {
    void start();
    void stop();
}

class Bus implements Transport {
    public void start() {
        System.out.println("Bus is starting...");
    }

    public void stop() {
        System.out.println("Bus is stopping...");
    }
}

class Train implements Transport {
    public void start() {
        System.out.println("Train is starting...");
    }

    public void stop() {
        System.out.println("Train is stopping...");
    }
}

public class Interface {
    public static void main(String[] args) {
        Transport bus = new Bus();
        bus.start();
        bus.stop();
        
        System.out.println();
        
        Transport train = new Train();
        train.start();
        train.stop();
    }
}
