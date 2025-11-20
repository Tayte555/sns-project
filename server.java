import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class ChatServer {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        String serverIP = "10.76.191.150";

        try {
            // 1. Get the InetAddress object for the specified IP
            InetAddress addr = InetAddress.getByName(serverIP);
            
            // 2. Create the ServerSocket, explicitly binding to the IP and Port
            ServerSocket listener = new ServerSocket(PORT, 0, addr);
            
            System.out.println("Server is running and listening on: " 
                               + serverIP + ":" + PORT);
            
            // Loop continuously to accept new clients
            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                // Start a new thread (ClientHandler) for this client
                // new ClientHandler(clientSocket).start(); 
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT + " or IP " + serverIP);
            e.printStackTrace();
        }
    }
}