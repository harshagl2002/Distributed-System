// import static org.junit.Assert.*;
// import org.junit.*;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.mockito.Mock;

// import java.io.BufferedReader;
// import java.io.PrintWriter;
// import java.io.StringReader;
// import java.io.StringWriter;
// import java.net.Socket;

// public class ClientHandlerTest {
//     @Mock
//     private Socket mockClientSocket;

//     private ClientHandler clientHandler;
//     private String serverIP = "localhost"; // Replace with your server IP

//     @Before
//     public void setUp() throws Exception {
//         MockitoAnnotations.initMocks(this);
//         clientHandler = new ClientHandler(mockClientSocket, serverIP);
//     }

//     @Test
//     public void testProcessQuery() throws Exception {
//         // Mock the input and output streams for the client socket
//         StringWriter writer = new StringWriter();
//         PrintWriter mockWriter = new PrintWriter(writer);
//         BufferedReader mockReader = new BufferedReader(new StringReader("your_expected_output_here"));

//         // Set up the behavior of the mock client socket
//         Mockito.when(mockClientSocket.getOutputStream()).thenReturn(new PrintWriter(writer, true));
//         Mockito.when(mockClientSocket.getInputStream()).thenReturn(new StringReader("your_input_data_here"));
//         Mockito.when(mockClientSocket.isConnected()).thenReturn(true);

//         // Perform the actual test by calling processQuery
//         String result = clientHandler.processQuery("[0-9]");
//         System.out.println("Client Handler Result = " + result);

//         // Assert the result or compare it to an expected output
//         assertEquals("your_expected_output_here", result);
//     }
    
// }
