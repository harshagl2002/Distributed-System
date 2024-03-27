import java.io.*;
import java.util.*;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MainApplication {
    private static final int HEARTBEAT_INTERVAL_MS = 1000;
    private static final int SIMULATE_GOSSIP_INTERVAL_MS = 6000;
    private static final int SUSPICION_THRESHOLD = 5;
    private static final int FAILURE_THRESHOLD = 10;

    private List<VMCommunication> vmCommunications;
    private Timer heartbeatTimer;
    private Timer simulateGossipTimer;
    private MemberList membershipList;
    private List<MemberInfo> mainMembershipList; // Maintain a separate membership list in MainApplication
    private Logger logger;
    private FileHandler fileHandler;
    
    public boolean suspicionMode = false;

    private static final int port_num = 8040;

    public MainApplication(boolean suspicionMode) {
        this.suspicionMode = suspicionMode;
        logger = Logger.getLogger("MyLogger");
        try {
            fileHandler = new FileHandler("vm.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.setLevel(Level.INFO);
        logger.log(Level.INFO, "HELO");
        // fileHandler.close();
        vmCommunications = new ArrayList<>();
        heartbeatTimer = new Timer();
        simulateGossipTimer = new Timer();
        membershipList = new MemberList();
        mainMembershipList = new ArrayList<>(); // Initialize the mainMembershipList

        initVMCommunications();
        startHeartbeatTask();
    }

    private void initVMCommunications() {
        
        // VMCommunication vm1 = new VMCommunication("172.22.158.155", 8080, 0, System.currentTimeMillis());
        // VMCommunication vm2 = new VMCommunication("172.22.94.155", 8080, 0, System.currentTimeMillis());
        VMCommunication vm1 = new VMCommunication("172.22.159.59", port_num, 0, System.currentTimeMillis()); // joey vm1
        VMCommunication vm2 = new VMCommunication("172.22.95.60", port_num, 0, System.currentTimeMillis());
        VMCommunication vm3 = new VMCommunication("172.22.157.181", port_num, 0, System.currentTimeMillis());
        VMCommunication vm4 = new VMCommunication("172.22.159.60", port_num, 0, System.currentTimeMillis());
        VMCommunication vm5 = new VMCommunication("172.22.95.61", port_num, 0, System.currentTimeMillis());
        VMCommunication vm6 = new VMCommunication("172.22.157.182", port_num, 0, System.currentTimeMillis());
        VMCommunication vm7 = new VMCommunication("172.22.159.61", port_num, 0, System.currentTimeMillis());
        VMCommunication vm8 = new VMCommunication("172.22.95.62", port_num, 0, System.currentTimeMillis());
        VMCommunication vm9 = new VMCommunication("172.22.157.183", port_num, 0, System.currentTimeMillis());
        VMCommunication vm10 = new VMCommunication("172.22.159.62", port_num, 0, System.currentTimeMillis());
        
        vmCommunications.add(vm1);
        vmCommunications.add(vm2);
        vmCommunications.add(vm3);
        vmCommunications.add(vm4);
        vmCommunications.add(vm5);
        vmCommunications.add(vm6);
        vmCommunications.add(vm7);
        vmCommunications.add(vm8);
        vmCommunications.add(vm9);
        vmCommunications.add(vm10);


        // Create MemberInfo objects for each VMCommunication
        mainMembershipList.add(vm1.getNodeInfo());
        mainMembershipList.add(vm2.getNodeInfo());
        mainMembershipList.add(vm3.getNodeInfo());
        mainMembershipList.add(vm4.getNodeInfo());
        mainMembershipList.add(vm5.getNodeInfo());
        mainMembershipList.add(vm6.getNodeInfo());
        mainMembershipList.add(vm7.getNodeInfo());
        mainMembershipList.add(vm8.getNodeInfo());
        mainMembershipList.add(vm9.getNodeInfo());
        mainMembershipList.add(vm10.getNodeInfo());
    }

    private void startHeartbeatTask() {
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendHeartbeatMessages();
                membershipList.detectSuspectedNodes(SUSPICION_THRESHOLD, HEARTBEAT_INTERVAL_MS);
                membershipList.removeDeadNodes(FAILURE_THRESHOLD, HEARTBEAT_INTERVAL_MS);
            }
        }, 0, HEARTBEAT_INTERVAL_MS);
    }

    private void startSimulateGossipTask() {
        simulateGossipTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                simulateGossip();
            }
        }, 0, SIMULATE_GOSSIP_INTERVAL_MS);
    }
    private void sendHeartbeatMessages() {
        String heartbeatMessage = "Heartbeat";
        for (VMCommunication vmCommunication : vmCommunications) {
            vmCommunication.sendMessage(heartbeatMessage);
            // membershipList.update
            // membershipList.updateTimestamp(vmCommunication.getNodeInfo().getId(), System.currentTimeMillis());
        }
    }

    public void printMembershipList() {
        membershipList.printMemberList();
        
    }

    public void printStartupMessage() {
        System.out.println("Start up a cluster of 5 nodes");

        for (int i = 0; i < vmCommunications.size(); i++) {
            VMCommunication vm = vmCommunications.get(i);
            System.out.println("Starting node: " + vm.getNodeInfo().getId());
            logger.log(Level.INFO, "Starting node: " + vm.getNodeInfo().getId());
            // System.out.print("with peers ");
            for (int j = 0; j < vmCommunications.size(); j++) {
                if (j != i) {
                    System.out.print(vmCommunications.get(j).getNodeInfo().getId());
                    if (j < vmCommunications.size() - 1) {
                        System.out.print(",");
                    }
                }
            }
            System.out.println();
        }
    }

    public void addNewNode(String ipAddress, int port) {
        VMCommunication newNode = new VMCommunication(ipAddress, port, 0, System.currentTimeMillis());
        vmCommunications.add(newNode);
        Thread thread = new Thread(() -> {
            try {
                handleServerResponses(newNode);
            } catch (IOException e) {
                e.printStackTrace();
            }});
        thread.start();
        // MemberInfo newNodeInfo = newNode.getNodeInfo();
        // mainMembershipList.add(newNodeInfo); // Update mainMembershipList with the new MemberInfo
        // membershipList.addOrUpdateNode(newNodeInfo.getId(), newNodeInfo.getLastHeartbeat(), newNodeInfo.getLastTimestamp(), suspicionMode);

        System.out.println("Added a new node: " + ipAddress);
        logger.log(Level.INFO,"Added a new node: " + ipAddress);
        
        
    }

    private MemberInfo findMemberInfo(String memberId) {
        for (MemberInfo memberInfo : mainMembershipList) {
            if (memberInfo.getId().equals(memberId)) {
                return memberInfo;
            }
        }
        return null; // MemberInfo not found
    }

    public void simulateGossip() {
        try {
            Thread.sleep(5000);

           for (VMCommunication vmCommunication : vmCommunications) {
                MemberInfo memberInfo = findMemberInfo(vmCommunication.getNodeInfo().getId());
                if (memberInfo != null && membershipList.containsID(vmCommunication.getNodeInfo().getId())) {
                    membershipList.addOrUpdateNode(memberInfo.getId(), memberInfo.getLastHeartbeat(), memberInfo.getLastTimestamp(), suspicionMode);
                }
            }

            // printMembershipList();
            System.out.println("Simulated gossip: Membership list updated for all nodes");
            logger.log(Level.INFO,"Simulated gossip: Membership list updated for all nodes");
            printMembershipList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void killRandomNode() {
        if (!vmCommunications.isEmpty()) {
            int randomIndex = (int) (Math.random() * vmCommunications.size());
            VMCommunication removedNode = vmCommunications.remove(randomIndex);

            // Remove from mainMembershipList
            mainMembershipList.removeIf(memberInfo -> memberInfo.getId().equals(removedNode.getNodeInfo().getId()));

            membershipList.removeNode(removedNode.getNodeInfo().getId());

            System.out.println("Killed peer: " + removedNode.getNodeInfo().getId());
        } else {
            System.out.println("No nodes to kill.");
        }
    }

    public void createReadFromServerThreads() {
        for (VMCommunication vmCommunication : vmCommunications) {
            Thread thread = new Thread(() -> {
                try {
                    handleServerResponses(vmCommunication);
                } catch (IOException e) {
                    e.printStackTrace();
                }});
            thread.start();
        }
    }

    public void handleServerResponses(VMCommunication vmCommunication) throws IOException {
        while(true) {
            try {
                // if (vmCommunication == null) {
                //     return;
                // }
                String msg = vmCommunication.receiveMessage();
                if(msg != null) {
                    int len = msg.length();
                    if(msg.length() < 14) {
                        continue;
                    }
                    String address = msg.substring(0, len-9);
                    if(membershipList.containsID(address)) {
                        // System.out.println("MESSSGE: "+ address);   
                        membershipList.updateTimestamp(address, System.currentTimeMillis());   
                        // if address is not in membershiplist --> add it          
                        // System.out.println(membershipList.getMembersAsList() + " "+ address);  
                    } else {
                        // add to list if not in (ONLY ADDING)
                        membershipList.addOrUpdateNode(address, 0, System.currentTimeMillis(), suspicionMode);
                        boolean inVMComm = false;
                        // System.out.println("DRESS "+ address);
                        for (VMCommunication vmc : vmCommunications) {
                            if (address.equals(vmc.getNodeInfo().getId())) {
                                inVMComm = true;
                            }
                        }
                        // System.out.println(address + " ADDYYY" + address.substring(0, address.length()-5) + "  " + address.substring(address.length()-4, address.length()));
                        if(!inVMComm) {
                            // addNewNode(address.substring(0, address.length()-5), port_num); // address.substring(address.length()-4, address.length())
                        }
                    }

 
                }


                
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }

        }


    }
    public static void main(String[] args) {
        String suspicionMode = "";
        if(args.length>0) {
            suspicionMode = args[0];
        }
        
        MainApplication mainApp;
        if(suspicionMode.equals("-s")) {
            mainApp = new MainApplication(true);
        } else {
            mainApp = new MainApplication(false);
        }

        mainApp.createReadFromServerThreads();

        mainApp.printStartupMessage();

        // mainApp.addNewNode("172.22.159.59", 8080);
        // mainApp.addNewNode("172.22.95.60", 8080);
        // mainApp.addNewNode("172.22.157.181", 8080);
        

        mainApp.printMembershipList();

        mainApp.simulateGossip();
        mainApp.startSimulateGossipTask();

        // mainApp.killRandomNode();
        mainApp.printMembershipList();
    }
}
