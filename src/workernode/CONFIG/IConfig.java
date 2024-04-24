package workernode.CONFIG;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

/**
 *
 * @author usula
 */
public interface IConfig { 
    public int getPort();
    public void setPort(int port);
    public String getHost();
    public void setHost(String host);
    public String getNodeName();
    public void setNodeName(String nodeName);
    public int getJobLimit();
    public void setJobLimit(int jobLimit);
}
