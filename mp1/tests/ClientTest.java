// import static org.junit.Assert.*;
// import org.junit.*;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.net.Socket;
// import java.net.ServerSocket;
// import java.util.List;

// public class ClientTest {
//     private Socket mockSocket;

//     private Client client;

//     @Before
//     public void setUp() {
//         client = new Client();
//     }

//     @Test
//     public void testNewThread() throws Exception {
//         // Mock the behavior of the socket
//         PrintWriter writer = new PrintWriter(mockSocket.getOutputStream());
//         BufferedReader reader = new BufferedReader(new InputStreamReader(mockSocket.getInputStream())); // Replace with an appropriate reader

//         // Mock the behavior of the serverAddresses list to contain your mock socket
//         List<Client.VMInfo> mockServerAddresses = new List<Client.VMInfo>();
//         Client.VMInfo mockVMInfo = new Client.VMInfo("mock_address", "1", true);
//         Mockito.when(mockServerAddresses.get(Mockito.anyInt())).thenReturn(mockVMInfo);
//         client.serverAddresses = mockServerAddresses;

//         // Call newThread with a query
//         client.newThread("[0-9]");

//         // You can't directly test thread behavior, but you can verify that the socket was used as expected
//         Mockito.verify(mockSocket, Mockito.times(1)).connect(Mockito.any(), Mockito.eq(3000));
//         Mockito.verify(mockSocket, Mockito.times(1)).getOutputStream();
//         Mockito.verify(mockSocket, Mockito.times(1)).getInputStream();
//         Mockito.verify(mockSocket, Mockito.times(1)).close();
//     }
    
// }
