class VoiceAssistant {
    public void voiceCommand(String command) {
        System.out.println(command);
    }

    public void voiceCommand(String command, String deviceName) {
        System.out.println(command + " for " + deviceName);
    }

    public void voiceCommand(String command, int duration) {
        System.out.println(command + " for " + duration + " minutes");
    }
}

public class Assistant {
    public static void main(String[] args) {
        VoiceAssistant assistant = new VoiceAssistant();
        assistant.voiceCommand("Turn on lights");
        assistant.voiceCommand("Turn on lights", "Living Room");
        assistant.voiceCommand("Set timer", 30);
    }
}
