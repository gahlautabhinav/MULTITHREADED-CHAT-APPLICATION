package com.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
* The Client class represents a chat client that connects to a chat server.
* It provides a graphical user interface for sending and receiving messages.
*/
public class Client implements ActionListener, Runnable {

    private JTextField text; // Text field for user input
    private JPanel a1; // Panel to display message
    private static Box vertical = Box.createVerticalBox(); // Vertical box to hold message panels
    private static JFrame f = new JFrame(); // Main application window
    private BufferedReader reader; // Reader for incoming messages
    private BufferedWriter writer; // Wirter for outgoing messages
    private String username; // Username for clients
    private Socket socket; // Scoket for communication with the server
    private Point initialClick; // Point to track mouse drag for window movement

    // Logger for logging events and errors
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    /**
    * Constructs a new Client instance and initializes the user interface.
    * Prompts the user for a username and connects to the chat server.
    */
    Client() {
        // Prompt user for username
        username = JOptionPane.showInputDialog("Enter your username: ");
        if (username == null || username.trim().isEmpty()) {
            username = "Anonymous"; // Default username if input is empty
        } else if (!isValidUsername(username)) {
            // Validate username
            JOptionPane.showMessageDialog(f, "Invalid username. Please use alphanumeric characters only.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Exit if username is invalid
        }

        f.setLayout(null); // Set layout to null for absolute positioning 
        setupUI(); // Setup the user interface
        connectToServer(); // Connect to the char server
    }

    /**
    * Validates the username to ensure it contains only alphanumeric characters and underscores.
    *
    * @param username the username to validate
    * @return true if the username is valid, false otherwise
    */
    private boolean isValidUsername(String username) {
        return username.matches("[a-zA-Z0-9_]+");
    }

    private void setupUI() {
        JPanel p1 = new JPanel();
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 450, 70);
        p1.setLayout(null);
        f.add(p1);

        ImageIcon i1 = new ImageIcon(getClass().getClassLoader().getResource("icons/3.png"));
        Image i2 = i1.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel back = new JLabel(i3);
        back.setBounds(5, 20, 25, 25);
        p1.add(back);

        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                exitApplication(); // Close gracefully on exit button click
            }
        });

        ImageIcon i4 = new ImageIcon(getClass().getClassLoader().getResource("icons/pfp.png"));
        Image i5 = i4.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel profile = new JLabel(i6);
        profile.setBounds(40, 5, 60, 60);
        p1.add(profile);

        JLabel name = new JLabel("Group");
        name.setBounds(110, 15, 100, 18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        p1.add(name);

        a1 = new JPanel();
        a1.setBackground(Color.WHITE);
        a1.setLayout(new BoxLayout(a1, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(a1);
        scrollPane.setBounds(5, 75, 450, 570);
        f.add (scrollPane);

        text = new JTextField();
        text.setBounds(5, 655, 310, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(text);

        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionPerformed(new ActionEvent(text, ActionEvent.ACTION_PERFORMED, null));
                }
            }
        });

        JButton send = new JButton("Send");
        send.setBounds(320, 655, 123, 40);
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(send);

        JButton exit = new JButton(" Exit");
        exit.setBounds(5, 700, 123, 40);
        exit.setBackground(Color.RED);
        exit.setForeground(Color.WHITE);
        exit.addActionListener(e -> exitApplication());
        exit.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(exit);

        f.setSize(450, 750);
        f.setLocation(20, 50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);

        f.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        f.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = f.getLocation().x;
                int thisY = f.getLocation().y;
                int xMoved = e.getXOnScreen() - initialClick.x - thisX;
                int yMoved = e.getYOnScreen() - initialClick.y - thisY;
                f.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        f.setVisible(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 2003);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.write(username);
            writer.write("\r\n");
            writer.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error connecting to server", e);
            JOptionPane.showMessageDialog(f, "Could not connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String message = text.getText();
            if (!message.trim().isEmpty()) {
                sendMessage(message);
                text.setText("");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending message", e);
        }
    }

    public void sendMessage(String message) throws IOException {
        String encryptedMessage = encryptMessage(message); // Encrypt the message before sending
        writer.write(encryptedMessage);
        writer.write("\r\n");
        writer.flush();
    }

    private String encryptMessage(String message) {
        // Simple encryption (for demonstration purposes)
        return new StringBuilder(message).reverse().toString();
    }

    public void displayMessage(String message) {
        JPanel p2 = formatLabel(message);
        vertical.add(p2);
        vertical.add(Box.createVerticalStrut(15));
        a1.add(vertical);
        a1.revalidate();
        a1.repaint();
    }

    public JPanel formatLabel(String message) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(messageLabel);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    public void run() {
        String msg;
        try {
            while ((msg = reader.readLine()) != null) {
                if (!msg.startsWith("USERLIST:")) {
                    displayMessage(msg);
                } else {
                    updateActiveUsers(msg); // Update the list of active users
                }
            }
        } catch (SocketException e) {
            logger.log(Level.WARNING, "Connection to server lost: " + e.getMessage());
            JOptionPane.showMessageDialog(f, "Connection to server lost. Please try reconnecting.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            exitApplication();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading message", e);
        }
    }

    private void updateActiveUsers(String msg) {
        String userList = msg.substring("USERLIST:".length());
        String[] users = userList.split(", ");
        StringBuilder userListDisplay = new StringBuilder("Active Users: ");
        for (String user : users) {
            userListDisplay.append(user).append(", ");
        }
        // Remove the last comma and space
        if (userListDisplay.length() > 2) {
            userListDisplay.setLength(userListDisplay.length() - 2);
        }
        displayMessage(userListDisplay.toString());
    }

    private void exitApplication() {
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing resources ", e);
        }
        System.exit(0); // Exit the application
    }

    public static void main(String[] args) {
        Client client = new Client();
        Thread thread = new Thread(client);
        thread.start();
    }
}
