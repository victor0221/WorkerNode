package workernode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jobsender.Job;

public class WorkerNode {
    private static int _jobCounter = 0;

    public static void main(String[] args) {
        //where the magic happens ;)
        PromptHandler pm = new PromptHandler();
        Config config = configDataCapture(pm);
        
        try {
            ServerSocket serverSocket = new ServerSocket(config.getPort());
            pm.handlePrompt("nodeRunning",config.getPort(), config.getNodeName(), null);
            ExecutorService executorService = Executors.newCachedThreadPool();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (_jobCounter >= config.getJobLimit()) {
                    pm.handlePrompt("maxJobs",0,null, null);
                    clientSocket.close();
                    continue;
                }

                // Start a new worker thread to handle the job
                WorkerThread workerThread = new WorkerThread(clientSocket, config.getNodeName());
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

        public WorkerThread(Socket clientSocket, String nodeName) {
            _clientSocket = clientSocket;
            _nodeName = nodeName;
        }
       
        @Override
        public void run() {
            PromptHandler pm = new PromptHandler();
            try {
                ObjectInputStream inputStream = new ObjectInputStream(_clientSocket.getInputStream());
                while (true) {
                    //get our data, in this case this would be job ms.
                    Job job = (Job) inputStream.readObject();
                    pm.handlePrompt("jobReceived",job.getJobTime(),job.getJobName(), null);
                    //mock procrssing, off the given job time.
                    Thread.sleep(job.getJobTime());      
                    //after job, break
                    pm.handlePrompt("jobComplete",job.getJobTime(), job.getJobName(), _nodeName);
                    break;
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                pm.handlePrompt("jobProccessErr",0,_nodeName, null);
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
    private static Config configDataCapture(PromptHandler pm){
         Scanner nodeNameCap = new Scanner(System.in);
        pm.handlePrompt("name",0,null, null);
        String activeNodeName = nodeNameCap.nextLine();
        Scanner jobLimitCap = new Scanner(System.in);
        pm.handlePrompt("limit",0,null, null);
        int activeJobLimit = jobLimitCap.nextInt();
        Scanner hostCap = new Scanner(System.in);
        pm.handlePrompt("host",0,null, null);
        String activeHost = hostCap.nextLine();
        Scanner portCap = new Scanner(System.in);
        pm.handlePrompt("port",0,null, null);
        int activePort = portCap.nextInt();
        return new Config(activePort,activeHost,activeNodeName,activeJobLimit);
    };
    
    private static void errorHandler(IOException e,PromptHandler pm){
            pm.handlePrompt("generalErr",0,null, null);
            Scanner myObj = new Scanner(System.in);
            pm.handlePrompt("pressY",0,null, null);
            String userInput = myObj.nextLine();
            if("y".equals(userInput) ){
               e.printStackTrace(); 
            }
    };
}
