import java.io.IOException;
import java.net.Socket;

public class SocketConnectionExample {
    public static void main(String[] args) {
        String remoteIpAddress = "127.0.0.1"; // Replace with the IP address of your VM
        int remotePort = 8080; // Replace with the port number you want to connect to

        
        for (int port = 1024; port < 65535; port++) {
            try {
                Socket socket = new Socket(remoteIpAddress, port);
                System.out.println("Success: Connected to " + port + " on port " + port);

                socket.close(); // Close the socket when done
                break;
            } catch (IOException e) {
                System.err.println("Error:" + port);
                
            }
        }
    }
}
