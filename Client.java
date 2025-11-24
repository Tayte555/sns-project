<<<<<<< Updated upstream
import java.net.Socket;
import java.io.IOException;

public class Client {
    private static final String SERVER_IP = "10.76.191.150";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try {
            // The Socket constructor attempts to connect to the specified IP and Port
            Socket socket = new Socket(SERVER_IP, SERVER_PORT); 
            
            System.out.println("Successfully connected to the server at: " 
                               + SERVER_IP + ":" + SERVER_PORT);
            
            // --- input/output streams here ---

        } catch (IOException e) {
            System.err.println("Could not connect to server. Check IP, Port, and Firewall.");
            e.printStackTrace();
        }
    }
}
=======
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {

        // LOAD THE TRUSTSTORE so we can verify the server's certificate
        System.setProperty("javax.net.ssl.trustStore", "mykeystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        try {
            // 1. Create SSL Socket Factory
            SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            // 2. Create the SSL Socket
            SSLSocket socket = (SSLSocket) sslFactory.createSocket(SERVER_IP, SERVER_PORT);

            // 3. Start the Handshake 
            socket.startHandshake();
            System.out.println("Successfully connected to the Tayte's server!");
            
            // --- THREAD 1: to LISTEN FOR INCOMING MESSAGES ---
            // separate thread so it doesn't block typing
            new Thread(new ServerListener(socket)).start();

            // --- MAIN THREAD: to SEND MESSAGES ---
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String userInput = scanner.nextLine();
                out.println(userInput); // Send to server
                // If user types 'exit', close the connection
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
            }

            socket.close();
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class to handle incoming messages
    private static class ServerListener implements Runnable {
        private Socket socket;

        public ServerListener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response;
                // While the connection is open, print whatever the server sends
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        }
    }
}
>>>>>>> Stashed changes
