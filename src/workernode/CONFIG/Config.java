package workernode.CONFIG;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author usula
 */
public class Config implements IConfig {
    
    private int _port;
    private String _host;
    private String _nodeName;
    private int _jobLimit;
    
    //my constructors,
    public Config(){
        
    };
    
    public Config(int port, String host,String nodeName, int jobLimit   ){
        _port = port;
        _host = host;
        _nodeName = nodeName;
        _jobLimit = jobLimit;
    };
    


    public int getPort() {
        return _port;
    };


    public void setPort(int port) {
        _port = port;
    };


    public String getHost() {
        return _host;
    };

    public void setHost(String host) {
        _host = host;
    };
    
    public String getNodeName() {
        return _nodeName;
    }

    public void setNodeName(String nodeName) {
        _nodeName = nodeName;
    }
    
    public int getJobLimit() {
        return _jobLimit;
    }

    public void setJobLimit(int jobLimit) {
        _jobLimit = jobLimit;
    }
    
}
