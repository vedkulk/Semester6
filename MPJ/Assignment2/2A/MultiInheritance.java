interface AudioPlayer {
    void playAudio();
    void stopAudio();
}

interface VideoPlayer {
    void playVideo();
    void stopVideo();
}

class SmartDevice implements AudioPlayer, VideoPlayer {
    public void playAudio() {
        System.out.println("Playing audio...");
    }

    public void stopAudio() {
        System.out.println("Audio stopped.");
    }

    public void playVideo() {
        System.out.println("Playing video...");
    }

    public void stopVideo() {
        System.out.println("Video stopped.");
    }
}

// Main class for testing
public class MultiInheritance {
    public static void main(String[] args) {
        SmartDevice device = new SmartDevice();
        device.playAudio();
        device.stopAudio();
        device.playVideo();
        device.stopVideo();
    }
}
