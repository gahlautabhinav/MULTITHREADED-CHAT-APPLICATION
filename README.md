# MULTITHREADED-CHAT-APPLICATION

**NAME**: ABHINAV GAHLAUT

**INTERN ID**: CT12FRU

**DOMAIN**: JAVA PROGRAMMING

**BATCH DURATION**: December 25th, 2024 to February 25th, 2025

# **GROUP CHAT: Multithreaded Chat Application**

This multithreaded chat application, built using Java sockets, enables real-time communication between multiple clients and a server. The server handles concurrent client connections, broadcasting messages and managing user sessions. With a user-friendly GUI, clients can send and receive messages seamlessly. This project demonstrates the implementation of client-server architecture, socket programming, and multithreading in Java.

**FILES INCLUDED IN REPO:**
- server (Directory containing Server build)
- client (Directory containing Client build)

**KEY FEATURES:**
- **Real-Time Messaging**: Experience instant communication as users can send and receive messages in real-time, fostering dynamic interactions.

- **Concurrent User Support**: The server is capable of handling multiple client connections simultaneously through multithreading, ensuring that all users can communicate without delays.

- **User Management**: Each user is required to provide a unique username upon connection. The application validates usernames to prevent duplicates, enhancing the user experience and maintaining order in conversations.

- **Intuitive Graphical User Interface (GUI)**: Built with Java Swing, the user interface is designed to be simple yet effective, allowing users to focus on their conversations without distractions. The layout is clean, and the functionality is straightforward.

- **Message Broadcasting**: Messages sent by any user are broadcasted to all connected clients, ensuring that everyone in the chat room stays informed and engaged.

- **Basic Message Encryption**: To add a layer of security, messages are encrypted (by reversing the string) before transmission. While this is a simple form of encryption, it serves as a foundation for implementing more robust security measures in the future.

**TOOLS/TECH. USED:**
- **Java**: The core programming language used for both the client and server applications.
- **Java Sockets**: For establishing network connections and facilitating communication between clients and the server.
- **Java Swing**: For creating the graphical user interface, providing a user-friendly experience.
- **Multithreading**: To manage multiple client connections concurrently, ensuring smooth operation.

**INSTALLATION AND USAGE:**
1. Prerequisites:
   - JDK (Java Development Kit)
   - Maven Build Tool

2. Clone the repo:
   - git clone https://github.com/yourusername/MULTITHREADED-CHAT-APPLICATION.git

3. Install both projects:
   - mvn clean install

4. Run the Server:
   - cd server
   - mvn exec:java

5. Run multiple Clients:
   - cd client
   - mvn exec:java

# **OUTPUT**
![Screenshot 2025-01-14 163101](https://github.com/user-attachments/assets/5e6568ea-d659-42c9-aa61-dc0cadd2b318)

![Screenshot 2025-01-14 163151](https://github.com/user-attachments/assets/1709bea0-1772-400f-8a35-91cf79a0e6cc)

![Screenshot 2025-01-14 163214](https://github.com/user-attachments/assets/23a4bd3c-d0b8-46b0-b759-cfd3fb1ebb48)

![Screenshot 2025-01-14 163256](https://github.com/user-attachments/assets/7243bc0b-59f8-47f5-9ec0-a85c58e08d37)

![Screenshot 2025-01-14 163321](https://github.com/user-attachments/assets/1dd6464a-2160-4c44-9d4a-e58d6c5f0b22)
