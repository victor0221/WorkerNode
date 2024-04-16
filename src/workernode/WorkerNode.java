package workernode;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class WorkerNode {
    public static void main(String[] args) {
        final String SERVER_IP = "localhost";
        final int SERVER_PORT = 42069;
        
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to load balancer.");
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
