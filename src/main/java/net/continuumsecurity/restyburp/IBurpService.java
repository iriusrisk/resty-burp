
package net.continuumsecurity.restyburp;

import net.continuumsecurity.restyburp.model.HttpMessage;
import net.continuumsecurity.restyburp.model.MessageType;
import net.continuumsecurity.restyburp.model.ScanIssueBean;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

 
 

public interface IBurpService {
	 
	//  Scan a given URL.  Will scan based on the traffic already captured by burp.
    void scan(String target) throws MalformedURLException;   
     
    //  Returns 0-100 depending on the completion of a scan
    int getPercentageComplete();   
     
    // Returns a list of ScanIssueBean's that represent the issues found so far for a given scan ID. 
    List<ScanIssueBean> getIssues(); 
     
    ScanQueueMap getScanQueue();
    // Return the whole proxy history.
    List<HttpMessage> getProxyHistory();
     
    // Return only the proxy history that matches the given URL. 
    List<HttpMessage> getProxyHistory(String url) throws Exception;
     
    // Restore Burp's state with that specified in the blank.burp.state file.
    void reset() throws Exception;

    byte[] makeRequest(String host,int port,boolean useHttps,byte[] request) throws Exception;
    void clearIssues() throws Exception;
     
    // Return the current configuration.
    Map<String,String> getConfig();
     
    // Replace the current configuration with the specified one.
    void setConfig(Map<String,String> newConfig);
     
    // Keep the current configuration and only update those items provided.
    void updateConfig(Map<String,String> newConfig);
      
    // Save the current config to a file
    void saveConfig(String filename);
     
    // Load the configuration from a file.
    void loadConfig(String filename);
    
    List<HttpMessage> findInHistory(String regex, MessageType type);
    
}
