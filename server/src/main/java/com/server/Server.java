package com.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The Server class represents a multi-threaded chat server that handles client connections.
 * It allows clients to send and receive messages and maintains a list of active users.
 */
public class Server implements Runnable {
    private Socket socket;
    private static final List<BufferedWriter> clients = new CopyOnWriteArrayList<>();
    private static final List<String> activeUsers = new CopyOnWriteArrayList<>();
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private BufferedWriter writer;
    private String username;

    /**
     * Constructs a new Server instance with the specified socket.
     *
     * @param socket the socket for the client connection
     */
    public Server(Socket socket) {
        this.socket = socket;
    }

    /**
     * Runs the server thread to handle client communication.
     * Reads messages from the client and broadcasts them to all connected clients.
     */
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            username = reader.readLine(); // Read the username from the client

            // Validate username
            if (!isValidUsername(username)) {
                logger.log(Level.WARNING, "Invalid username: " + username);
                writer.write("Invalid username. Disconnecting.\r\n");
                writer.flush();
                socket.close();
                return;
            }

            clients.add(writer);
            activeUsers.add(username); 
            broadcastUser_List();

            String data;
            while ((data = reader.readLine()) != null) {
                String decryptedMessage = decryptMessage(data); // Decrypt the message
                logger.log(Level.INFO, "Received: " + decryptedMessage);
                broadcastMessage(String.format("[%s] %s: %s", getCurrentTime(), username, decryptedMessage));
            }
        } catch (SocketException e) {
            logger.log(Level.WARNING, "Client disconnected unexpectedly: " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error in communication", e);
        } finally {
            cleanup(); // Clean up resources on exit
        }
    }

    /**
     * Validates the username to ensure it is not null, empty, and contains only valid characters.
     *
     * @param username the username to validate
     * @return true if the username is valid, false otherwise
     */
    private boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty() && username.matches("[a-zA-Z0-9_]+");
    }

    /**
     * Encrypts a message using a simple encryption method (for demonstration purposes).
     *
     * @param message the message to encrypt
     * @return the encrypted message
     */
    private String encryptMessage(String message) {
        // Simple encryption (for demonstration purposes)
        return new StringBuilder(message).reverse().toString(); // Reverse the message as a form of "encryption"
    }

    /**
     * Decrypts a message using a simple decryption method (for demonstration purposes).
     *
     * @param message the message to decrypt
     * @return the decrypted message
     */
    private String decryptMessage(String message) {
        // Simple decryption (for demonstration purposes)
        return new StringBuilder(message).reverse().toString(); // Reverse the message back to original
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param message the message to broadcast
     */
    private void broadcastMessage(String message) {
        for (BufferedWriter bw : clients) {
            try {
                bw.write(message);
                bw.write("\r\n");
                bw.flush();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error sending message to client", e);
                clients.remove(bw);  // Remove client if an error occurs
            }
        }
    }

    /**
     * Broadcasts the list of active users to all connected clients.
     */
    private void broadcastUser_List() {
        String userList = String.join(", ", activeUsers);
        String userListMessage = "USERLIST:" + userList;
        for (BufferedWriter bw : clients) {
            try {
                bw.write(userListMessage);
                bw.write("\r\n");
                bw.flush();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error sending user list to client", e);
                clients.remove(bw); // Remove client if an error occurs
            }
        }
    }

    /**
     * Cleans up resources when a client disconnects.
     */
    private void cleanup() {
        try {
            if (writer != null) {
                clients.remove(writer);
                activeUsers.remove(username);
                broadcastUser_List();
            }
            socket.close(); // Close the socket
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing socket", e);
        }
    }

    /**
     * Gets the current time formatted as HH:mm:ss.
     *
     * @return the current time as a string
     */
    private String getCurrentTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        return sdf.format(new java.util.Date());
    }

    /**
     * The main method to start the chat server.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Setup logging to a file
        try {
            FileHandler fileHandler = new FileHandler("server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler); // Add file handler to logger
            logger.setLevel(Level.ALL); // Set logger level to ALL
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error setting up file logging", e);
        }
    
        ExecutorService executorService = Executors.newFixedThreadPool(10); // Thread pool for managing client connections
        try (ServerSocket serverSocket = new ServerSocket(2003)) {
            serverSocket.setSoTimeout(5000); // Set timeout for accepting connections
            while (true) {
                try {
                    Socket socket = serverSocket.accept(); // Accept a new client connection
                    Server server = new Server(socket); // Create a new server instance for the client
                    executorService.execute(server);
                } catch (SocketTimeoutException e) {
                    System.out.println("Waiting for client connection...");
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error starting server", e);
        } finally {
            executorService.shutdown(); // Shutdown the executor service
        }
    }
}
