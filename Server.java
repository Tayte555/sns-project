import java.net.Socket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int PORT = 5000;

    // list to keep track of all active clients
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {

        // LOAD THE KEYSTORE

        // This tells Java where to find the certificate and the password to open it

        System.setProperty("javax.net.ssl.keyStore", "mykeystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        System.setProperty("javax.net.ssl.trustStore", "servertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        try {

            // 1. Create the SSL ServerSocket Factory

            SSLServerSocketFactory sslFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            // 2. Create the SSL ServerSocket, passing just port to listen to all interfaces

            SSLServerSocket listener = (SSLServerSocket) sslFactory.createServerSocket(PORT);
            listener.setNeedClientAuth(true);
            
            System.out.println("SECURE Server is running on port: " + PORT);

            System.out.println("Waiting for SSL handshake...");

            // Loop continuously to accept new clients

            while (true) {

                Socket clientSocket = listener.accept();

                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a handler for this specific client

                ClientHandler clientThread = new ClientHandler(clientSocket);

                // Add to list and start the thread

                clients.add(clientThread);

                new Thread(clientThread).start();

            }

        } catch (IOException e) {

            System.err.println("Could not listen on port " + PORT + " or IP ");

            e.printStackTrace();

        }

    }

    // This method sends a message to EVERYONE connected except the sender

    public static void broadcast(String message, ClientHandler sender) {

        for (ClientHandler client : clients) {

            if (client != sender) {

                client.sendMessage(message);

            }

        }

    }

    // ClientHandler class to manage individual client connections

    private static class ClientHandler implements Runnable {

        private Socket socket;

        private PrintWriter out;

        private BufferedReader in;

        public ClientHandler(Socket socket) {

            this.socket = socket;

        }

        @Override

        public void run() {

            try {

                // Setup streams

                // 'true' in PrintWriter enables AUTO-FLUSH after each println to ensure
                // messages are not stuck in buffer

                out = new PrintWriter(socket.getOutputStream(), true);

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Ask for username

                out.println("Welcome to the VlasChat! Enter your username:");

                String username = in.readLine();

                System.out.println("User joined: " + username);

                // Announce to everyone

                Server.broadcast("[SERVER]: " + username + " has joined the chat!", this);

                // Listen for messages indefinitely

                String message;

                while ((message = in.readLine()) != null) {

                    String formattedMessage = "[" + username + "]: " + message;

                    System.out.println(formattedMessage); // Print on server console

                    Server.broadcast(formattedMessage, this); // Send to other clients

                }

            } catch (IOException e) {

                System.err.println("Error in Client Handler: " + e.getMessage());

            } finally {

                try {

                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        // Helper method to send a message to this specific client

        public void sendMessage(String msg) {

            out.println(msg);

        }

    }
}// End of Server class