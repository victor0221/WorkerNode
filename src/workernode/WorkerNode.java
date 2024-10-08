/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package workernode;

import workernode.HELPERS.PromptHandler;
import workernode.CONFIG.Config;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jobsender.JOB_TEMPLATE.Job;

/**
 *
 * @author usula
 */
public class WorkerNode {

    private static int _jobCounter = 0;
    private static int _jobCapacity = 0;

    public WorkerNode() {

    }

    public void runWorkerNode() {
        //where the magic happens ;)
        PromptHandler pm = new PromptHandler();
        Config config = configDataCapture(pm);
        _jobCapacity = config.getJobLimit();

        try {
            ServerSocket serverSocket = new ServerSocket(config.getNodePort());
            pm.handlePrompt("nodeRunning", config.getNodePort(), config.getNodeName(), null);
            pm.handlePrompt("waitingForLb", 0, null, null);

            // checking registration with the Load Balancer, it keeps on trying until registered comes back in response
            while (!registerWithLoadBalancer(config, pm)) {
                try {
                    // Delay before retrying
                    Thread.sleep(500); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            ExecutorService executorService = Executors.newCachedThreadPool();
            
//            Sharing initial capacity with the LB to correct any differences from LB user input
            shareCapacity(config, pm);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (_jobCounter >= config.getJobLimit()) {
                    pm.handlePrompt("maxJobs", 0, null, null);
//                   If no capacity, send to job queue
                    sendToJobQueue(clientSocket, config, pm);
                    clientSocket.close();
                    continue;
                }

                // Start a new worker thread to handle the job
                WorkerThread workerThread = new WorkerThread(clientSocket, config);
                executorService.submit(workerThread);
                // Increment active jobs count
                _jobCounter++;
            }
        } catch (IOException e) {
            errorHandler(e, pm);
        }
    }

    public static class WorkerThread extends Thread {

        private Socket _clientSocket;
        private String _nodeName;
        private String _nodeHost;
        private int _nodePort;
        private String failedJob;
        private String _lbHost;
        private int _lbPort;

        public WorkerThread(Socket clientSocket, Config config) {
            _clientSocket = clientSocket;
            _nodeName = config.getNodeName();
            _nodeHost = config.getNodeHost();
            _nodePort = config.getNodePort();
            _lbHost = config.getLbHost();
            _lbPort = config.getLbPort();
        }

        @Override
        public void run() {
            PromptHandler pm = new PromptHandler();
            try {
                ObjectInputStream inputStream = new ObjectInputStream(_clientSocket.getInputStream());
                while (true) {
                    //get our data, in this case this would be job ms.
                    Job job = (Job) inputStream.readObject();
                    failedJob = job.getJobName();
                    pm.handlePrompt("jobReceived", job.getJobTime(), job.getJobName(), null);
                    //mock procrssing, off the given job time.
                    Thread.sleep(job.getJobTime());
                    
//                  Sending message to lb on job completion
                    Socket lbSocket = new Socket(_lbHost, _lbPort);
                    ObjectOutputStream lbOut = new ObjectOutputStream(lbSocket.getOutputStream());
                    lbOut.writeUTF("JOB_COMPLETION");

                    String completionMessage = "Job Completed: " + job.getJobName() + " completed by " + _nodeName + " in " + job.getJobTime() + "ms";
                    lbOut.writeUTF(_nodeName);
                    lbOut.writeUTF(_nodeHost);
                    lbOut.writeInt(_nodePort);
                    lbOut.writeUTF(completionMessage);
                    lbOut.flush();
                    
                    //after job, break
                    pm.handlePrompt("jobComplete", job.getJobTime(), job.getJobName(), _nodeName);
                    break;
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                pm.handlePrompt("jobProccessErr", 0, failedJob, _nodeName);
            } finally {
                try {
                    _clientSocket.close();
                } catch (IOException e) {
                    errorHandler(e, pm);
                }

                //after success we decrement the job counter
                _jobCounter--;
            }
        }
    }

    //helper functions
    private static Config configDataCapture(PromptHandler pm) {
        String activeNodeName = captureString("name", pm);
        int activeJobLimit = captureInt("limit", pm);   
        String activeHost = captureString("host", pm);
        int activePort = captureInt("port", pm);  
        String lbHost = captureString("lbHost", pm);
        int lbPort = captureInt("lbPort", pm);
        return new Config(activePort, activeHost, activeNodeName, activeJobLimit, lbPort, lbHost);
    }

    ;
    
    private static int captureInt(String promptCode, PromptHandler pm) {
        int validInt = 0;
        boolean validInput = false;
        while (!validInput) {
            Scanner scanner = new Scanner(System.in); 
            pm.handlePrompt(promptCode, 0, null, null);
            if (scanner.hasNextInt()) {
                validInt = scanner.nextInt(); 
                validInput = true; 
            } else {
                pm.handlePrompt("invalidInt", 0, null, null);
                scanner.next(); 
            }
        }
        return validInt;
    }
    
    ;
    
    private static String captureString(String promptCode, PromptHandler pm) {
        String validString = "";
        boolean validInput = false;
        while (!validInput) {
            Scanner scanner = new Scanner(System.in);
            pm.handlePrompt(promptCode, 0, null, null);
            validString = scanner.nextLine().trim();
            if (!validString.isEmpty()) {
                validInput = true;
            } else {
                pm.handlePrompt("invalidString", 0, null, null);
            }
        }
        return validString;
    }
    
    private static void errorHandler(Exception e, PromptHandler pm) {
        pm.handlePrompt("generalErr", 0, null, null);
        Scanner myObj = new Scanner(System.in);
        pm.handlePrompt("pressY", 0, null, null);
        String userInput = myObj.nextLine();
        if ("y".equals(userInput)) {
            e.printStackTrace();
        }
    }
;
    
    private boolean registerWithLoadBalancer(Config config, PromptHandler pm) {
        try {
//            Sending message to Lb to check registration
            Socket regSocket = new Socket(config.getLbHost(), config.getLbPort());
            ObjectOutputStream regLbOut = new ObjectOutputStream(regSocket.getOutputStream());
            regLbOut.writeUTF("REGISTER");
            regLbOut.writeUTF(config.getNodeName());
            regLbOut.writeUTF(config.getNodeHost());
            regLbOut.writeInt(config.getNodePort());
            regLbOut.flush();
            
//            Getting response back from LB
            ObjectInputStream regLbIn = new ObjectInputStream(regSocket.getInputStream());
            String response = regLbIn.readUTF();
            
            if ("REGISTERED".equals(response)) {
                pm.handlePrompt("registeredWithLb", 0, null, null);
                regSocket.close();
                return true;
            } else{
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    ;
    
    private void sendToJobQueue(Socket clientSocket, Config config, PromptHandler pm){

        try {
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            Job queuedJob = (Job) inputStream.readObject();

            Socket queueSocket = new Socket(config.getLbHost(), config.getLbPort());
            ObjectOutputStream queueLbOut = new ObjectOutputStream(queueSocket.getOutputStream());
            queueLbOut.writeUTF("ADD_TO_JOB_QUEUE");
            queueLbOut.writeObject(queuedJob);
            queueLbOut.flush();
            pm.handlePrompt("jobQueued", queuedJob.getJobTime(), queuedJob.getJobName(), null);
            clientSocket.close();  
        } catch (IOException | ClassNotFoundException e) {
            errorHandler(e, pm);
        }
    }
    
    ;
    
    private void shareCapacity(Config config, PromptHandler pm) {

        Socket capacitySocket = null;
        try {
            capacitySocket = new Socket(config.getLbHost(), config.getLbPort());
            ObjectOutputStream capacityLbOut = new ObjectOutputStream(capacitySocket.getOutputStream());
            capacityLbOut.writeUTF("NODE_CAPACITY");
            capacityLbOut.writeUTF(config.getNodeName());
            capacityLbOut.writeUTF(config.getNodeHost());
            capacityLbOut.writeInt(config.getNodePort());
            capacityLbOut.writeInt(_jobCapacity);
            capacityLbOut.flush();
        } catch (IOException e) {
            pm.handlePrompt("capacityCommsErr", 0, e.getMessage(), null);
        } finally {
            try {
                if (capacitySocket != null) {
                    capacitySocket.close();
                }
            } catch (IOException e) {
                pm.handlePrompt("socketCloseErr", 0, e.getMessage(), null);
            }
        }
    }
    
}
