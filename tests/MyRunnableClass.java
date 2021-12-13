package tests;

public class MyRunnableClass implements Runnable {
    public boolean stop = false;

    @Override
    public void run() {
        System.out.println("Thread running");

        while (!stop) {
            System.out.println("still running");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
            }
        }

        System.out.println("Thread stopping");
    }

    public void halt() {
        System.out.println("The thread will now stop.");
        stop = true;
    }
}