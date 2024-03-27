import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleServer  {
    private static Socket vmSocket;
    final static int portNumber = 8040;
    public static void main(String[] args) {
         // Choose a port number for your server

        System.out.println("Entering server");
        String vmAddress = "";
        try {
            vmAddress = InetAddress.getLocalHost().getHostAddress(); // keep track of MainApplication corresponding w/ server and forwarding messages there
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                try  {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(clientSocket.getInetAddress().getHostAddress() + " " + vmAddress);
                    if(clientSocket.getInetAddress().getHostAddress().equals(vmAddress)) {
                        vmSocket = clientSocket;
                        System.out.println("SAME SOCKET");
                    }
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                    // Handle client request
                    Thread thread = new Thread(() -> {
                        try {
                            
                            handleClient(clientSocket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }});
                    thread.start();
                    
                } catch (IOException e) {
                e.printStackTrace();
            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException {
        // Input stream to receive data from the client
        InputStream inputStream = clientSocket.getInputStream();

        // Output stream to send data back to the client
        // OutputStream outputStream = clientSocket.getOutputStream();

        // Read data from the client
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String receivedData = new String(buffer, 0, bytesRead);
                System.out.println("Received from client " + clientSocket.getInetAddress() + ": " + receivedData);
                String sendData = ""+receivedData +clientSocket.getInetAddress().getHostAddress() + ":" + portNumber;
                if(vmSocket != null) {
                    // vmSocket.getOutputStream().write(receivedData.getBytes());
                    // vmSocket.getOutputStream().println(sendData);
                    vmSocket.getOutputStream().write(sendData.getBytes());
                                        // vmSocket.getOutputStream().write(clientSocket.getInetAddress().getHostAddress().getBytes());
                }
                
                // Send a response back to the client
                // String responseData = "Server response: Hello, client!";
                // outputStream.write(responseData.getBytes());
                
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        // Close the client socket when done
        clientSocket.close();
        System.out.println("Client disconnected.");
    }
}
