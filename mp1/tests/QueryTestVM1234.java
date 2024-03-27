import static org.junit.Assert.*;
import org.junit.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/*
 * Desciption -
 * These unit tests use some frequent and infrequent strings to query VMs 1-10, where
 * only VMs 1-4 are actually active.
 * Instructions - 
 * Run servers on VMs 1-4
 * Run this test
 */

public class QueryTestVM1234 {
    private Client myClient; 
    @Before
    public void setUp() {
        myClient = new Client();
        myClient.serverAddresses.clear();
        myClient.serverAddresses.add(new Client.VMInfo("172.22.158.155","1", true));
        myClient.serverAddresses.add(new Client.VMInfo("172.22.94.155", "2", true));
        myClient.serverAddresses.add(new Client.VMInfo("172.22.156.156", "3",true));
        myClient.serverAddresses.add(new Client.VMInfo("172.22.158.156", "4",true));
        myClient.serverAddresses.add(new Client.VMInfo("172.22.94.156", "5", true));
        myClient.serverAddresses.add(new Client.VMInfo("172.22.156.157", "6",true));
        myClient.serverAddresses.add(new Client.VMInfo("172.22.158.157", "7", true));
        myClient.serverAddresses.add(new Client.VMInfo("172.22.94.157", "8",true));
        myClient.serverAddresses.add(new Client.VMInfo("172.22.156.158", "9", true));
        myClient.serverAddresses.add(new Client.VMInfo("172.22.158.158", "10", true));
    }

    @Test 
    public void checkCorrectVMServersMarkedActive() throws Exception {
        myClient.newThread("test");
        // if this fails, the rest of the tests will fail
        assertTrue("VM1 active", myClient.serverAddresses.get(0).getActive());
        assertTrue("VM2 active", myClient.serverAddresses.get(1).getActive());
        assertTrue("VM3 active", myClient.serverAddresses.get(2).getActive());
        assertTrue("VM4 active", myClient.serverAddresses.get(3).getActive());

        assertFalse("VM5 inactive", myClient.serverAddresses.get(4).getActive());
        assertFalse("VM6 inactive", myClient.serverAddresses.get(5).getActive());
        assertFalse("VM7 inactive", myClient.serverAddresses.get(6).getActive());
        assertFalse("VM8 inactive", myClient.serverAddresses.get(7).getActive());
        assertFalse("VM9 inactive", myClient.serverAddresses.get(8).getActive());
        assertFalse("VM10 inactive", myClient.serverAddresses.get(9).getActive());
    }

    @Test
    public void frequentQueryA() throws Exception {
        myClient.newThread("a");
        System.out.println("System total matching lines: " + myClient.getTotalCount());
        assertEquals("Query a result", 283553+267938+268804+270917, myClient.getTotalCount());
    }

    @Test
    public void frequentQueryW() throws Exception {
        myClient.newThread("w");
        System.out.println("System total matching lines: " + myClient.getTotalCount());
        assertEquals("Query w result", 232748+220049+220577+222376, myClient.getTotalCount());
    }

   
    @Test
    public void frequentQueryCom() throws Exception {
        myClient.newThread("com");
        System.out.println("System total matching lines: " + myClient.getTotalCount());
        assertEquals("Query com result", 176215+166375+166628+167602, myClient.getTotalCount());
    }

    @Test
    public void frequentQueryApp() throws Exception {
        myClient.newThread("app");
        System.out.println("System total matching lines: " + myClient.getTotalCount());
        assertEquals("Query app result", 84527+80131+80156+80472, myClient.getTotalCount());
    }

    @Test
    public void infrequentQueryOrg() throws Exception {
        myClient.newThread("org");
        System.out.println("System total matching lines: " + myClient.getTotalCount());
        assertEquals("Query org result", 29313+27838+28014+28198, myClient.getTotalCount());
    }
    
    @Test
    public void infrequentQueryMiller() throws Exception {
        myClient.newThread("miller");
        System.out.println("System total matching lines: " + myClient.getTotalCount());
        assertEquals("Query miller result", 3759+3581+3592+3617, myClient.getTotalCount());
    }

    @Test 
    public void infrequentQuerySnyder() throws Exception {
        myClient.newThread("snyder");
        System.out.println("System total matching lines: " + myClient.getTotalCount());
        assertEquals("Query snyder result", 544+543+569+523, myClient.getTotalCount());
    }

    @Test 
    public void infrequentQueryWang() throws Exception {
        myClient.newThread("wang");
        System.out.println("System total matching lines: " + myClient.getTotalCount());
        assertEquals("Query wang result", 229+208+230+228, myClient.getTotalCount());
    }


}