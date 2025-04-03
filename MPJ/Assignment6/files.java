package Assignment6;

import java.util.concurrent.*;

class FileDownloader implements Runnable {
    private String file;

    public FileDownloader(String file) {
        this.file = file;
    }

    public void run() {
        long threadId = Thread.currentThread().getId(); // Get thread ID
        for (int i = 1; i <= 10; i++) {
            System.out.println("Thread ID: " + threadId + " - " + file + " download: " + (i * 10) + "%");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Thread ID: " + threadId + " - " + file + " download complete");
    }
}

public class files {
    public static void main(String[] args) {
        ExecutorService ex = Executors.newFixedThreadPool(3); // 3 threads
        String[] files = { "file1", "file2", "file3", "file4", "file5" };

        for (String f : files) {
            ex.execute(new FileDownloader(f));
        }

        ex.shutdown();
    }
}