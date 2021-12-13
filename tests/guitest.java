package tests;

// import statement  
import javax.swing.*;
import java.awt.event.*;

public class guitest {
    JFrame mainFrame;
    JButton b1, b2, b3, b4;
    JTextArea logArea;

    // constructor
    guitest() {
        b1 = new JButton("test 1");
        b2 = new JButton("test 2");
        b3 = new JButton("test 3");
        b4 = new JButton("test 4");
        logArea = new JTextArea();

        mainFrame = new JFrame();
        mainFrame.setLayout(null);
        mainFrame.setSize(1280, 720);
        mainFrame.setVisible(true);

        b1.setBounds(50, 50 + 15 * 0, 150, 50);
        b2.setBounds(50, 100 + 15 * 1, 150, 50);
        b3.setBounds(50, 150 + 15 * 2, 150, 50);
        b4.setBounds(50, 200 + 15 * 3, 150, 50);
        logArea.setBounds(50 + 150 + 50, 50 + 15 * 0, 600, 400);

        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button 1 pressed");
            }
        });

        mainFrame.add(b1);
        mainFrame.add(b2);
        mainFrame.add(b3);
        mainFrame.add(b4);
        mainFrame.add(logArea);
    }

    // main method
    public static void main(String[] args) {
        new guitest();
    }

}