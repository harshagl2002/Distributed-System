// Client.java
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client {
    static boolean debug_mode = false; // set to true for additional prints
    List<VMInfo> serverAddresses = new ArrayList<>();
    int port = 8080; // choose any free port but needs to match whats in server.java
    int totalCount;

    // total count is total number of matching lines for the whole system based on client query
    public int getTotalCount() {
        return this.totalCount;
    }

    // add count from one vm to the system total
    public void addToTotalCount(int count) {
        this.totalCount += count;
    }

    // new query, count needs to be 0
    public void resetTotalCount() {
        this.totalCount = 0;
    }


    public Client() {
                // in order VM 01-10
        serverAddresses.add(new VMInfo("172.22.158.155","1", true));
        serverAddresses.add(new VMInfo("172.22.94.155", "2", true));
        serverAddresses.add(new VMInfo("172.22.156.156", "3",true));
        serverAddresses.add(new VMInfo("172.22.158.156", "4",true));
        serverAddresses.add(new VMInfo("172.22.94.156", "5", true));
        serverAddresses.add(new VMInfo("172.22.156.157", "6",true));
        serverAddresses.add(new VMInfo("172.22.158.157", "7", true));
        serverAddresses.add(new VMInfo("172.22.94.157", "8",true));
        serverAddresses.add(new VMInfo("172.22.156.158", "9", true));
        serverAddresses.add(new VMInfo("172.22.158.158", "10", true));
    }

    // spins up and executes multiple threads to query the servers with new grep command
    public void newThread(String query) {
        try {
            Lock queueLock = new ReentrantLock();
            List<Thread> threads = new ArrayList<>();

            for (VMInfo machine : serverAddresses) {
                String address = machine.getAddress();
                String number = machine.getNumber();
                boolean active = machine.getActive();
                
                if(active) { // only try to connect if the server is thought to be active
                    if(debug_mode) {
                        System.out.println("Attempting to create client socket with VM" + number + " " + address + ":" + port + 
                        ". Currently marked as active = " + active);
                    }
                    Thread thread = new Thread(() -> {
                        try(Socket socket = new Socket();) {
                            socket.connect(new InetSocketAddress(address, port), 3000); // max wait time is 3 seconds
                            try(PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())) ;) {
                                if(debug_mode) {
                                    System.out.println("Successfully created client socket for VM" + number +" " + address + ":" + port);
                                }
                                queueLock.lock();

                                if (query != null) {
                                    writer.println(query);
                                    String line;

                                    int lineCount = 0;
                                    int vmCount = 0;
                                    while ((line = reader.readLine()) != null) {
                                        if(lineCount == 2) {
                                            String[] resultArray = line.split(" ");
                                            vmCount = Integer.valueOf(resultArray[3]);
                                            this.addToTotalCount(vmCount);
                                        }
                                        System.out.println(line);
                                        lineCount++;
                                    }
                                }
                                
                                queueLock.unlock();
                                socket.close();
                                writer.close();
                                reader.close();
                            } catch (Exception e) {
                                System.out.println("Failed to read query");
                            }
                        }
                        catch(IOException e) {
                            machine.setActive(false); // mark inactive

                            if(debug_mode) {
                                System.out.println("Failed to create client socket in time for VM"+ number + " " + address + ":" + port + ". Marking server inactive " +
                                machine.getActive());
                            // e.printStackTrace();
                            }
                        }
                    });

                    threads.add(thread);
                    thread.start();
                }
            }
            // Wait for all threads to finish
            for (Thread thread : threads) {
                thread.join();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // LinkedHashMap<String, Boolean>  serverAddresses = new LinkedHashMap<String, Boolean> ();
        Client myClient = new Client();
        

        // Manually add VM IP Addresses 
        if(debug_mode) {
            System.out.println("Running in debug mode. ");
            // serverAddresses.add(new VMInfo("127.0.0.1", "laptop", true)); // add laptop local
        }

        Scanner scanner = new Scanner(System.in);

        // prompt user for input
        System.out.println("Enter a query: ('exit' to quit)");
        String query;

        while(!(query = scanner.nextLine()).equals("exit")) {
            query = query.strip(); // remove any white space at beginning or end
            if(query.length() >= 1) { // length should be at least 1 if something was typed
                myClient.newThread(query);
                System.out.println("System total matching lines: " + myClient.getTotalCount());
                myClient.resetTotalCount();
            }
            else {
                System.out.println("Cannot run empty query.");
            }
            System.out.println("Enter a query ('exit' to quit):");
       }
       
    }

    // holds the ip address, vm number, and whether the server is currently up and running
    public static class VMInfo {
        private String address;
        private String number;
        private Boolean active; 

        VMInfo(String address, String number, Boolean active) {
            this.address = address;
            this.number = number; 
            this.active = active;
        }

        // address and number shouldn't need a setter because it shouldn't change after initialization
        public String getAddress() {
            return this.address;
        }

        public String getNumber() {
            return this.number;
        }

        public Boolean getActive() {
            return this.active; 
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}
