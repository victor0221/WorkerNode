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

    private int _nodePort;
    private String _nodeHost;
    private String _nodeName;
    private int _jobLimit;
    private int _lbPort;
    private String _lbHost;

    //my constructors,
    public Config() {

    }

    ;
    
    public Config(int nodePort, String nodeHost, String nodeName, int jobLimit, int lbPort, String lbHost) {
        _nodePort = nodePort;
        _nodeHost = nodeHost;
        _nodeName = nodeName;
        _jobLimit = jobLimit;
        _lbPort = lbPort;
        _lbHost = lbHost;
    }

    ;
    


    public int getNodePort() {
        return _nodePort;
    }

    ;
    
    public int getLbPort() {
        return _lbPort;
    }

    ;


    public void setNodePort(int nodePort) {
        _nodePort = nodePort;
    }

    ;
    
    public void setLbPort(int lbPort) {
        _lbPort = lbPort;
    }

    ;


    public String getNodeHost() {
        return _nodeHost;
    }

    ;
    
    public String getLbHost() {
        return _lbHost;
    }

    ;

    public void setNodeHost(String nodeHost) {
        _nodeHost = nodeHost;
    }

    ;
    
    public void setLbHost(String lbHost) {
        _lbHost = lbHost;
    }

    ;
    
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
