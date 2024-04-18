package workernode;

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
    
    //my constructors,
    public Config(){
        
    };
    
    public Config(int port, String host){
        _port = port;
        _host = host;
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
    
}
