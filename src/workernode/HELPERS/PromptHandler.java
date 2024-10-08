/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package workernode.HELPERS;

/**
 *
 * @author usula
 */
public class PromptHandler {

    public PromptHandler() {

    }

    public void handlePrompt(String code, int optionalInt, String optionalString, String optionalString2) {
        switch (code) {
            case "nodeRunning":
                System.out.println("Node " + optionalString + " is running (port: " + optionalInt + ")");
                break;
            case "waitingForLb":
                System.out.println("Waiting for Load Balancer to Start...");
                break;
            case "registeredWithLb":
                System.out.println("Registered with Load Balancer.");
                break;
            case "capacityCommsErr":
                System.err.println("Failed to send capacity information: " + optionalString);
                break;
            case "threadErr":
                System.err.println("Thread interrupted: " + optionalString);
                break;
            case "maxJobs":
                System.out.println("MAX JOB LIMIT REACHED!");
                break;
            case "jobReceived":
                System.out.println("Received " + optionalString + " with duration " + optionalInt + "ms");
                break;
            case "jobComplete":
                System.out.println("Job completed: " + optionalString + " completed in " + optionalInt + "ms" + "(" + optionalString2 + ")");
                break;
            case "jobQueued":
                System.out.println("JOB QUEUED: " + optionalString + " with duration " + optionalInt + "ms sent to Job queue.");
                break;
            case "jobProccessErr":
                System.out.println("Error processing " + optionalString + " on node "+ optionalString2);
                break;
            case "name":
                System.out.println("Enter your Node name: ");
                break;
            case "limit":
                System.out.println("Enter job limit on node: ");
                break;
            case "host":
                System.out.println("Enter your host number (e.g. 'localhost'): ");
                break;
            case "port":
                System.out.println("Enter port number: ");
                break;
            case "lbHost":
                System.out.println("Enter host number that you will use for Load Balancer(e.g. 'localhost'): ");
                break;
            case "lbPort":
                System.out.println("Enter port number that you will use for Load Balancer: ");
                break;
            case "generalErr":
                System.out.println("AN ERROR HAS OCCURED!");
                break;
            case "pressY":
                System.out.println("enter 'y' to view stack:");
                break;
            case "socketCloseErr":
                System.err.println("Error closing socket: " + optionalString);
                break;
            case "invalidInt":
                System.out.println("Invalid input. Please enter a valid integer.");
                break;
            case "invalidString":
                System.out.println("No input. Please enter something, this cannot be left blank.");
                break;
        }
    }
}
