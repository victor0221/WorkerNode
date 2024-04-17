package workernode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class WorkerNode {
    public static void main(String[] args) {
        
        Scanner hostCap = new Scanner(System.in);
        System.out.println("Enter your host number (e.g. 'localhost'): ");
        String activeHost = hostCap.nextLine();
        Scanner portCap = new Scanner(System.in);
        System.out.println("Enter port number: ");
        int activePort = portCap.nextInt();
        Config config = new Config(activePort, activeHost);
        
        try {
            //establishes connection with LB
           Socket socket = new Socket(config.getHost(), config.getPort());
           System.out.println("Connected to load balancer.");
            while (true) {

            }
            // still needs doing....
        } catch (IOException e) {
            System.out.println("Connection FAILED! Ensure load balancer is started!");
            
            //debug helper
            Scanner myObj = new Scanner(System.in);
            System.out.println("press 'y' to view stack: ");
            String userInput = myObj.nextLine();
            if("y".equals(userInput) ){
               e.printStackTrace(); 
            }
        }
    }
    
}
