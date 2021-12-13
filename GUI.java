import javax.swing.*;
import java.awt.event.*;

public class GUI {
    public static void main(String[] args) {
        JFrame exchangeWindow = new JFrame("exchange");
        JFrame user1Window = new JFrame("user1");
        JFrame user2Window = new JFrame("user2");

        exchangeWindow.setSize(800, 600);
        exchangeWindow.setVisible(true);
        exchangeWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        user1Window.setSize(600, 400);
        user1Window.setVisible(true);

        user2Window.setSize(600, 400);
        user2Window.setVisible(true);
    }
}
