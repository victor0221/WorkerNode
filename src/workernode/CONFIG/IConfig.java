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
    public int getNodePort();
    public void setNodePort(int nodePort);
    public String getNodeHost();
    public void setNodeHost(String nodeHost);
    public String getNodeName();
    public void setNodeName(String nodeName);
    public int getJobLimit();
    public void setJobLimit(int jobLimit);
    public int getLbPort();
    public void setLbPort(int lbPort);
    public String getLbHost();
    public void setLbHost(String lbHost);
}
