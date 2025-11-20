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