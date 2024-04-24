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
    public PromptHandler(){
        
    }
    
    public void handlePrompt(String code, int optionalInt, String optionalString, String optionalString2){
        switch(code){
            case "nodeRunning":
                System.out.println("Node "+optionalString+" is running (port: "+optionalInt+")");
                break;
            case "maxJobs":
                System.out.println("MAX JOB LIMIT REACHED!");
                break;
            case "jobReceived":
                System.out.println("Received " + optionalString + " with duration " + optionalInt + "ms");
                break;
            case "jobComplete":
                System.out.println(optionalString + " completed in " + optionalInt + "ms" + "(" + optionalString2 + ")");
                break;
            case "jobProccessErr":
                System.out.println("Error processing job on node "+optionalString);
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
            case "generalErr":
                System.out.println("AN ERROR HAS OCCURED!");
                break;
            case "pressY":
                System.out.println("enter 'y' to view stack:");
                break;
        }
    }
}
