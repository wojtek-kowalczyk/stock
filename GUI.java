import javax.swing.*;
import java.awt.event.*;

public class GUI {
    public static void main(String[] args) {
        JFrame exchangeWindow = new JFrame();
        JFrame user1Window = new JFrame();
        JFrame user2Window = new JFrame();

        exchangeWindow.setSize(800, 600);
        exchangeWindow.setVisible(true);

        user1Window.setSize(600, 400);
        user1Window.setVisible(true);

        user2Window.setSize(600, 400);
        user2Window.setVisible(true);
    }

}

class GUI_old {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int PADDING_WINDOW = 50;
    private static final int PADDING_COMPONENT = 20;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 50;

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();
        // create, configure and add components
        // ORDER LIST LABEL
        JLabel orderListLabel = new JLabel("Order list");
        orderListLabel.setBounds(
                PADDING_WINDOW,
                PADDING_WINDOW,
                BUTTON_WIDTH,
                BUTTON_HEIGHT);
        mainFrame.add(orderListLabel);

        // ORDER LIST AREA
        JTextArea orderListArea = new JTextArea();
        orderListArea.setBounds(
                PADDING_WINDOW,
                PADDING_WINDOW + BUTTON_HEIGHT,
                BUTTON_WIDTH,
                BUTTON_HEIGHT * 10);
        orderListArea.setEditable(false);
        orderListArea.setText("THIS IS DUMMY TEXT ORDERS WILL BE HERE");
        orderListArea.setLineWrap(true);
        orderListArea.setWrapStyleWord(true);
        mainFrame.add(orderListArea);

        // MAIN EXCHANGE AREA
        JTextArea exchangeTextArea = new JTextArea();
        exchangeTextArea.setBounds(
                PADDING_WINDOW + BUTTON_WIDTH + PADDING_COMPONENT,
                PADDING_WINDOW,
                BUTTON_WIDTH * 4,
                BUTTON_HEIGHT * 8);
        exchangeTextArea.setEditable(false);
        exchangeTextArea.setText("THIS IS DUMMY TEXT EXCHANGE ASSETS WITH PRICES");
        exchangeTextArea.setLineWrap(true);
        exchangeTextArea.setWrapStyleWord(true);
        mainFrame.add(exchangeTextArea);

        // USER INFO AREA
        JTextArea userInfoArea = new JTextArea();
        userInfoArea.setBounds(
                PADDING_WINDOW + BUTTON_WIDTH * 5 + PADDING_COMPONENT * 2,
                PADDING_WINDOW,
                BUTTON_WIDTH * 2,
                BUTTON_HEIGHT * 4);
        userInfoArea.setEditable(false);
        userInfoArea.setText("THIS IS DUMMY TEXT ACTIVE USER INFO WILL BE HERE");
        userInfoArea.setLineWrap(true);
        userInfoArea.setWrapStyleWord(true);
        mainFrame.add(userInfoArea);

        // USER INTERACTION SECTION
        // ASSET NAME INPUT FIELD
        JTextField assetNameField = new JTextField("input asset name");
        assetNameField.setBounds(
                PADDING_WINDOW + BUTTON_WIDTH * 5 + PADDING_COMPONENT * 2,
                PADDING_WINDOW + BUTTON_HEIGHT * 4 + PADDING_COMPONENT,
                BUTTON_WIDTH,
                (int) (BUTTON_HEIGHT * 0.5));
        mainFrame.add(assetNameField);

        // ALLOW PARTIAL CHECKBOX
        JCheckBox allowPartialCB = new JCheckBox("Allow Partial?", true);
        allowPartialCB.setBounds(
                PADDING_WINDOW + BUTTON_WIDTH * 5 + PADDING_COMPONENT * 2,
                PADDING_WINDOW + BUTTON_HEIGHT * 4 + PADDING_COMPONENT + (int) (BUTTON_HEIGHT * 0.5),
                BUTTON_WIDTH,
                (int) (BUTTON_HEIGHT * 0.5));
        mainFrame.add(allowPartialCB);

        // IS BUY ORDER CHECKBOX
        JCheckBox buyOrderCB = new JCheckBox("Buy Order", true);
        buyOrderCB.setBounds(
                PADDING_WINDOW + BUTTON_WIDTH * 5 + PADDING_COMPONENT * 2,
                PADDING_WINDOW + BUTTON_HEIGHT * 4 + PADDING_COMPONENT
                        + (int) (BUTTON_HEIGHT * 0.5) * 2,
                BUTTON_WIDTH,
                (int) (BUTTON_HEIGHT * 0.5));
        mainFrame.add(buyOrderCB);

        // initialize a window
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(WIDTH, HEIGHT);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
    }
}
