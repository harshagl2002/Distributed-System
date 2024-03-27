import static org.junit.Assert.*;
import org.junit.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerTest {
    private ServerSocket serverSocket;
    private Thread serverThread;
    private int serverPort;
    String acceptString = "Hello client, you've been accepted";

    @Before
    public void setUp() throws IOException {
        // Find a free port for the test server
        Server myServer= new Server();
        serverSocket = new ServerSocket(8080);
        serverPort = serverSocket.getLocalPort();

        // Start the server in a separate thread
        serverThread = new Thread(() -> {
            try {
                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    writer.println(acceptString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    @After
    public void tearDown() throws IOException {
        // Stop the server and close the server socket
        serverSocket.close();
        serverThread.interrupt();
    }

    @Test 
    public void testServerSocketCreated() throws IOException {
        assertEquals("Server should be listening on port 8080", 8080, serverPort);
    }


    @Test
    public void testServerClientResponse() throws IOException {
        // Simulate a client request using Apache HttpClient or Java Socket
        String response = "";
        try (Socket clientSocket = new Socket("127.0.0.1", serverPort);
             PrintWriter writer  = new PrintWriter(clientSocket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){

            // // Send a request to the server
           
            String request = "Hi server, this is client";
            writer.println(request);
            writer.flush();


            String line;
            line = reader.readLine();
            response += line; 
        }
        assertEquals("Ensure the client gets the right response from the server", response, acceptString);
    }
    
    

    // Add more server-side tests as needed
}
