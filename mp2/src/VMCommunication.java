import java.io.*;
import java.util.List;
import java.net.*;

public class VMCommunication {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String ipAddress;
    private int port;
    private int heartbeatCounter;
    private long lastTimestamp;

    public VMCommunication(String ipAddress, int port, int heartbeatCounter, long lastTimestamp) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.heartbeatCounter = heartbeatCounter;
        this.lastTimestamp = lastTimestamp;

        try {
            // Initialize socket connection to the specified VM
           
            this.socket = new Socket(ipAddress, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            // Handle connection errors
            e.printStackTrace();
            System.out.println("Error with " + ipAddress);
            
        }
    }



    public void sendMessage(String message) {
        // Send a message to the connected VM
        if(!message.equals(null)) {
            out.println(message);
        }
        
    }

    public String receiveMessage() throws IOException {
        // Receive a message from the connected VM
        return in.readLine();
    }

    public MemberInfo getNodeInfo() {
        MemberInfo minfo = new MemberInfo(ipAddress + ":" + Integer.toString(port), heartbeatCounter, lastTimestamp);
        return minfo;
    }

    public int getHeartbeatCounter() {
        return heartbeatCounter;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setHeartbeatCounter(int heartbeatCounter) {
        this.heartbeatCounter = heartbeatCounter;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    // public void updateMembershipList(List<MemberInfo> updatedMembershipList) {
    //     // Update the membership list on this node
    //     // You can update your local membership list with the provided updatedMembershipList

    //     // Example logic (replace with your actual logic):
    //     for (MemberInfo updatedMemberInfo : updatedMembershipList) {
    //         membershipList.addOrUpdateNode(updatedMemberInfo.getId(), updatedMemberInfo.getLastHeartbeat(), updatedMemberInfo.getLastTimestamp());
    //     }

    //     System.out.println("Membership list updated on this node.");
    // }

    public void close() throws IOException {
        // Close the socket and streams when done
        in.close();
        out.close();
        socket.close();
    }
}
