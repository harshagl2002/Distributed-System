import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


// ssh s
public class Server {

    // finds the ip address of the machine running the server
    public static String findIPAddress() {
        String address = null; 
        try {
            
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            processBuilder.command("bash", "-c", " hostname -I");

            Process process = processBuilder.start();
            BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();

            try {
                address = processReader.readLine(); // should only ever have to read the first line
                address = address.substring(0, address.length()-1); // trim off newline
            }
            catch(Exception e) {
                System.out.println("Nothing was returned from hostname -I");
            }
        
            
        } catch (Exception e) {
            System.out.println("Failed to run hostname -I, make sure you're running a VM");
        }
        
        return address;
    }

    public static void main(String[] args) {
        // choose any free port to listen for connections on
        int port = 8080;

        // create the server socket and start listening on the port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            String serverIP = findIPAddress();
            System.out.println("Server is running and listening " + serverIP + ":" + port);
            // wait for and accept client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // inet address gives /xx.xxxx so need to remove / from beginning
                // String serverAddress = (serverSocket.getInetAddress()).toString().substring(1);
                Thread thread = new Thread(new ClientHandler(clientSocket, serverIP));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final Lock processQueryLock = new ReentrantLock();
    boolean debug_mode = false; // enable to add helpful print statements
    String serverIP;
    

    public ClientHandler(Socket clientSocket, String serverIP) {
        this.clientSocket = clientSocket;
        this.serverIP = serverIP;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String query = reader.readLine();
            String result = processQuery(query); 
            writer.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Name: processQuery
     * Description: Takes the phrase entered by the user, runs the grep command on log files for all connected servers
     * using the query, and returns a string with the result
     * Input: query phrase to use as argument for grep, the machine number
     * Returns: String with file name, line number, and line content for each instance that query phrase was found
     */
    private String processQuery(String longQuery) {
        try {
                // System.out.println("Entered process query");
                processQueryLock.lock();
                if(debug_mode) {
                    System.out.println("query lock locked");
                }
                // log file should always be in this location
                String directoryPath = "/home/mjmoy2/mp1/logfiles/";
                String vmNumber = getVMNumber(serverIP);
                String fileName = "vm" + vmNumber + ".log";

                ProcessBuilder processBuilder = new ProcessBuilder();
                // Split the user query input by whitespace to separate flags and query argument
                String[] flagsArray = longQuery.split("\\s+");  
                // the last argument should be query, everything before should be flags
                String query = flagsArray[flagsArray.length -1]; 
                // Construct the command with individual flags
                StringBuilder commandBuilder = new StringBuilder("grep");
                commandBuilder.append(" -nr -E");
                String flag;
                for (int i = 0; i < flagsArray.length-1; i++) {
                    flag = flagsArray[i];
                    // ignore the -f flag and -c flag, all other flags get added to the command
                    // -c flag makes everything return result as 1 matching line
                    if((!flag.equals("-f") && (!flag.equals("-c")))) {
                        commandBuilder.append(" ").append(flagsArray[i]);
                    }
                }
                commandBuilder.append(" '").append(query).append("' ").append(directoryPath).append(fileName);
                processBuilder.command("bash", "-c", commandBuilder.toString());
                Process process = processBuilder.start();
                BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                int counter = 0; // count number of matching lines in this file
                // read and format the results from running the command
                while ((line = processReader.readLine()) != null) {
                    if(debug_mode) {
                        String[] parts = line.split(":", 3);
                        if (parts.length == 3) {
                            String lineNumber = parts[1];
                            String content = parts[2];
                            result.append("Debug mode - File: ").append(fileName).append(", Line: ").append(lineNumber).append("\n").append(content).append("\n");
                        }
                        else if(parts.length == 2) {
                            String lineNumber = parts[0];
                            String content = parts[1];
                            result.append("Debug mode -File: ").append(fileName).append(", Line: ").append(lineNumber).append("\n").append(content).append("\n");
                        }
                        else {
                            result.append("Debug mode - " + line);
                        }
                    }
                    counter++;  
                }
                result.append("---Results for VM" + vmNumber + "---\n");
                result.append("File: " + fileName + "\n");
                result.append("Total matching lines: " + counter + "\n");
                int exitCode = process.waitFor();
                processReader.close();

                
                processQueryLock.unlock();
                if(debug_mode) {
                    System.out.println("query lock released");
                }

                if (exitCode == 0) {
                    return result.toString();
                } else {
                    // Include error output from the command if available
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    StringBuilder errorOutput = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorOutput.append(errorLine).append("\n");
                    }
                    errorReader.close();

                    if (errorOutput.length() > 0) {
                        return "Command exited with non-zero status code: " + exitCode + "\nError output:\n" + errorOutput.toString();
                    } else {
                        return "VM" + vmNumber + " - Command exited with non-zero status code: " + exitCode + " :matching query not found";
                    }
                }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    // helper method to take the address of the server and get the vm number 
    // this way vm number can be displayed with the result when printed to client
    private String getVMNumber(String address) {
        String number = null;
         switch(address) {
                case "172.22.158.155":
                    number = "1";
                    break;
                case "172.22.94.155":
                    number = "2";
                    break;
                case "172.22.156.156":
                    number = "3";
                    break;
                case "172.22.158.156":
                    number = "4";
                    break; 
                case "172.22.94.156":
                    number = "5";
                    break;
                case "172.22.156.157":
                    number = "6";
                    break; 
                case "172.22.158.157":
                    number = "7";
                    break;
                case "172.22.94.157":
                    number = "8";
                    break;
                case "172.22.156.158":
                    number = "9";
                    break;
                case "172.22.158.158":
                    number = "10";
                    break;
                default:
                // do nothing
                    break;
                }
 
        return number;
    }
}
