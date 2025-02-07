
class SmartDevice {
    String deviceName;
    int powerConsumption;

    public SmartDevice(String deviceName, int powerConsumption) {
        this.deviceName = deviceName;
        this.powerConsumption = powerConsumption;
    }

    public void displayDetails() {
        System.out.println("Device Name: " + deviceName);
        System.out.println("Power Consumption: " + powerConsumption + " W");
    }
}

interface RemoteControl {
    void turnOn();
    void turnOff();
}

interface InternetConnectivity {
    void connectToWiFi();
}

class SmartTV extends SmartDevice implements RemoteControl, InternetConnectivity {
    public SmartTV(String deviceName, int powerConsumption) {
        super(deviceName, powerConsumption);
    }

    public void turnOn() {
        System.out.println("Turning on the TV...");
    }

    public void turnOff() {
        System.out.println("Turning off the TV...");
    }

    public void connectToWiFi() {
        System.out.println("Connecting TV to WiFi...");
    }
}

class SmartLight extends SmartDevice implements RemoteControl {
    public SmartLight(String deviceName, int powerConsumption) {
        super(deviceName, powerConsumption);
    }

    public void turnOn() {
        System.out.println("Turning on the light...");
    }

    public void turnOff() {
        System.out.println("Turning off the light...");
    }
}

public class HybridInheritance {
    public static void main(String[] args) {
        SmartTV tv = new SmartTV("Samsung TV", 100);
        tv.displayDetails();
        tv.turnOn();
        tv.connectToWiFi();
        
        System.out.println();
        
        SmartLight light = new SmartLight("Philips Light", 10);
        light.displayDetails();
        light.turnOn();
    }
}
