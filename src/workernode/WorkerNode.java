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
            
//            Sharing capacity with the LB
            new Thread(() -> shareCapacity(config, pm)).start();

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
                WorkerThread workerThread = new WorkerThread(clientSocket, config.getNodeName(), config.getLbHost(), config.getLbPort());
                executorService.submit(workerThread);
                // Increment active jobs count
                _jobCounter++;
                _jobCapacity--;
            }
        } catch (IOException e) {
            errorHandler(e, pm);
        }
    }

    public static class WorkerThread extends Thread {

        private Socket _clientSocket;
        private String _nodeName;
        private String failedJob;
        private String _lbHost;
        private int _lbPort;

        public WorkerThread(Socket clientSocket, String nodeName, String lbHost, int lbPort) {
            _clientSocket = clientSocket;
            _nodeName = nodeName;
            _lbHost = lbHost;
            _lbPort = lbPort;
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

                //after success we decrement the job counter and increment the capacity
                _jobCounter--;
                _jobCapacity++;
            }
        }
    }

    //helper functions
    private static Config configDataCapture(PromptHandler pm) {
        Scanner nodeNameCap = new Scanner(System.in);
        pm.handlePrompt("name", 0, null, null);
        String activeNodeName = nodeNameCap.nextLine();
        Scanner jobLimitCap = new Scanner(System.in);
        pm.handlePrompt("limit", 0, null, null);
        int activeJobLimit = jobLimitCap.nextInt();
        Scanner hostCap = new Scanner(System.in);
        pm.handlePrompt("host", 0, null, null);
        String activeHost = hostCap.nextLine();
        Scanner portCap = new Scanner(System.in);
        pm.handlePrompt("port", 0, null, null);
        int activePort = portCap.nextInt();
        Scanner lbHostCap = new Scanner(System.in);
        pm.handlePrompt("lbHost", 0, null, null);
        String lbHost = lbHostCap.nextLine();
        Scanner lbPortCap = new Scanner(System.in);
        pm.handlePrompt("lbPort", 0, null, null);
        int lbPort = lbPortCap.nextInt();
        return new Config(activePort, activeHost, activeNodeName, activeJobLimit, lbPort, lbHost);
    }

    ;
    
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
        
        while (!Thread.currentThread().isInterrupted()) {
            if (_jobCapacity >= 1) {
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
                try {
                    // Do not remove this delay
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  
                    pm.handlePrompt("threadErr",0, e.getMessage(), null);
                    return;
                }
            }
        }
    }
        
}
