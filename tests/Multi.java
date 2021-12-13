package tests;

import java.util.Scanner;

public class Multi {
    public static void main(String[] args) {
        System.out.println("running program");
        MyRunnableClass r = new MyRunnableClass();
        Thread t = new Thread(r, "custom thread");
        t.start();
        // throw new RuntimeException();
        // afther thwoing the VM with is still running, thread t still executing
        boolean stop = false;
        Scanner sc = new Scanner(System.in);
        while (!stop) {
            System.out.println("ASKING INPUT");
            String line = sc.nextLine().trim();
            System.out.println("echo: " + line);
            if (line.equals("stop")) {
                System.out.println("user requested stop");
                r.halt();
                stop = true;
            }

        }
        sc.close();
    }
}