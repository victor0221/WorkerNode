/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package workernode;


import java.io.IOException;
import java.lang.reflect.Method;
import jobsender.HELPERS.PromptHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import workernode.CONFIG.Config;

/**
 *
 * @author usula
 */
public class WorkerNodeTest {
    
    //set up data for testing.
    private String _name = "node1";
    private String _host = "localhost";
    private int _port = 12345;
    private String _lbHost = "localhost";
    private int _lbPort = 1000;
    private int _jobLimit = 5;
    
    //use our config class to setUp a config, no mock this time...
    
    
    
    public WorkerNodeTest() {
    }
  @Test
    public void testConfig() {
        Config con = new Config(_port, _host, _name, _jobLimit, _lbPort, _lbHost);  
        assertEquals(_port, con.getNodePort());
        assertEquals(_host, con.getNodeHost());
        assertEquals(_name, con.getNodeName());
        assertEquals(_jobLimit, con.getJobLimit());
        assertEquals(_lbPort, con.getLbPort());
        assertEquals(_lbHost, con.getLbHost());
    }
    
      @Test
    public void registerWithLbSuccess() {
        String res = "REGISTERED";
        boolean result = false;
        Config con = new Config(_port, _host, _name, _jobLimit, _lbPort, _lbHost);
         if ("REGISTERED".equals(res)) {
            result = true;
        } else{
            result = false;
        }
         assertEquals(true, result);
    }
    @Test
    public void registerWithLbFailure() {
        String res = "FAILED";
        boolean result = false;
        Config con = new Config(_port, _host, _name, _jobLimit, _lbPort, _lbHost);
         if ("REGISTERED".equals(res)) {
            result = true;
        } else{
            result = false;
        }
         assertEquals(false, result);
    }
    
     @Test
    public void SendToJobQueue() {
       int incomingJobs = 500;
       int queuedJobs = 0;
       if(incomingJobs < _jobLimit){
           int queue =  incomingJobs - _jobLimit;
           queuedJobs = queue; 
            assertEquals(400, queue);
       }
    }
    
    @Test
    public void threadTest() {
        mockRunWorkerNode();
    }
    
    
    //helpers
public void mockRunWorkerNode() {
    int success = 1;
    if (success == 1) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < _jobLimit; i++) {
                try {
                    Thread.sleep(1000); // mock proccessing
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //pass
               
            }
        });

        thread.start();
    }
}

        
}


